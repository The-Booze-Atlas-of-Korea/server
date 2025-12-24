package com.ssafy.sulmap.infra.external.openai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.openai.client.OpenAIClient;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponseCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class GptRecommendClient {

    private static final String MODEL = "gpt-5.2";
    private static final long MAX_OUTPUT_TOKENS = 800;

    // pool 라인 포맷: "B|id=123|..."
    private static final Pattern ID_PATTERN = Pattern.compile("(?m)^B\\|id=(\\d+)\\b");

    private final OpenAIClient _OpenAIClient;

    /**
     * 2차 최종 추천: 후보 풀(pool) 안에서 topK개를 순위대로 뽑고 reasons를 반환
     *
     * @param topK 보통 10
     * @param ctx  "CTX|g=...|a=...|ts=...|w=...|md=...|q=..." 한 줄
     * @param pool "B|id=...|c=...|oi=...|n=..." 여러 줄 (보통 30~50)
     */
    public RecommendOutput rankTop(int topK, String ctx, String pool) {
        if (topK <= 0) throw new IllegalArgumentException("topK must be positive");
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(pool, "pool");

        List<Long> allowed = extractBarIds(pool);
        if (allowed.isEmpty()) {
            throw new IllegalArgumentException("No barIds found in pool. Expected lines like: B|id=123|...");
        }

        String input = stageInstructions(topK, ctx, pool);

        StructuredResponseCreateParams<RecommendOutput> params = ResponseCreateParams.builder()
                .model(MODEL)
                .instructions(systemInstructions())
                .input(input)
//                .maxOutputTokens(MAX_OUTPUT_TOKENS)
                .text(RecommendOutput.class)
                .build();

        var response = _OpenAIClient.responses().create(params);

        RecommendOutput out = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(msg -> msg.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No structured output returned"));

        return normalize(out, topK, allowed);
    }

    // ===== Prompt =====

    private String stageInstructions(int topK, String ctx, String pool) {
        return String.join("\n",
                "아래 CTX와 후보(B 라인)만 보고, 최종 Top " + topK + "을 순위대로 선택하고 각 항목의 reasons를 작성해라.",
                "",
                "[INPUT]",
                ctx,
                pool,
                "",
                "[OUTPUT JSON]",
                "{",
                "  \"top\": [",
                "    { \"barId\": 123, \"reasons\": [\"...\", \"...\"] }",
                "  ]",
                "}",
                "",
                "제약:",
                "- top은 정확히 " + topK + "개",
                "- barId 중복 금지",
                "- 반드시 B 라인에 존재하는 barId만 선택",
                "- reasons는 각 항목당 2~3개",
                "- 각 reason은 45자 이내",
                "- JSON만 출력(설명/마크다운 금지)",
                "- top의 순서는 추천 순위(앞이 더 추천)",
                "",
                "힌트:",
                "- 사용 가능한 후보 정보는 B 라인의 c(카테고리), oi(영업정보요약), n(이름요약), menu(메뉴) 뿐이다.",
                "- CTX의 q(요청), w(날씨), ts(시간)를 반영해 취향/상황 적합도를 판단하라."
        );
    }

    /**
     * ✅ 외부 상식/추론은 보완적으로 허용 (너 요구 반영)
     * 단, 후보 밖 선택/새 barId 생성은 절대 금지.
     */
    private String systemInstructions() {
        return String.join("\n",
                "너는 \"최종 추천\" 랭커다.",
                "반드시 입력으로 주어진 B 라인(후보) 안에서만 선택한다.",
                "후보에 없는 술집을 만들거나 추측하지 마라. (새 barId 생성 금지)",
                "일반 상식/배경지식/추론은 보완적으로 활용 가능하다.",
                "단, 최종 선택은 CTX와 B 라인 정보가 우선이며, B 라인과 모순되는 가정은 하지 마라.",
                "출력은 반드시 JSON만. 마크다운/코드펜스/설명 문장 금지.",
                "",
                "우선순위 가이드:",
                "1) CTX.q(요청)과 B의 c/oi/n이 잘 맞는가",
                "2) CTX.ts(시간대)에 맞게 oi(영업정보)상 무리 없어 보이는가",
                "3) CTX.w(날씨)가 나쁘면 이동/대기 부담이 적을 것으로 추정되는 선택을 선호(확정 정보 없으면 과도한 단정 금지)",
                "4) 비슷하면 다양성(카테고리/스타일)도 약간 고려"
        );
    }

    // ===== Structured Output schema =====

    public static final class RecommendOutput {
        @JsonPropertyDescription("Top-K recommendations in ranked order.")
        public List<Item> top;

        public RecommendOutput() {}
        public RecommendOutput(List<Item> top) { this.top = top; }
    }

    public static final class Item {
        @JsonPropertyDescription("Must be one of the barIds in the given B lines.")
        public long barId;

        @JsonPropertyDescription("2~3 short reasons, each <= 45 chars.")
        public List<String> reasons;

        public Item() {}
        public Item(long barId, List<String> reasons) {
            this.barId = barId;
            this.reasons = reasons;
        }
    }

    // ===== Normalization / Validation =====

    private RecommendOutput normalize(RecommendOutput out, int topK, List<Long> allowedInOrder) {
        Set<Long> allowedSet = new HashSet<>(allowedInOrder);

        // 1) 후보 밖 제거 + 중복 제거 + 순서 유지
        LinkedHashMap<Long, Item> uniq = new LinkedHashMap<>();
        List<Item> items = (out == null || out.top == null) ? List.of() : out.top;

        for (Item it : items) {
            if (it == null) continue;
            long id = it.barId;
            if (!allowedSet.contains(id)) continue;
            if (uniq.containsKey(id)) continue;

            uniq.put(id, new Item(id, normalizeReasons(it.reasons)));
            if (uniq.size() >= topK) break;
        }

        // 2) 부족하면 후보 순서대로 채우기(폴백)
        for (Long id : allowedInOrder) {
            if (uniq.size() >= topK) break;
            if (uniq.containsKey(id)) continue;
            uniq.put(id, new Item(id, List.of("요청 조건에 무난", "후보 내 상위")));
        }

        // 3) topK로 자르기
        List<Item> normalized = new ArrayList<>(topK);
        int count = 0;
        for (Item it : uniq.values()) {
            normalized.add(it);
            count++;
            if (count >= topK) break;
        }

        return new RecommendOutput(normalized);
    }

    private List<String> normalizeReasons(List<String> reasons) {
        if (reasons == null || reasons.isEmpty()) {
            return List.of("요청 조건에 부합", "상황에 적합");
        }

        List<String> out = new ArrayList<>(3);
        for (String r : reasons) {
            if (r == null) continue;
            String t = r.trim();
            if (t.isBlank()) continue;

            // 45자 제한(서버에서도 방어)
            if (t.length() > 45) t = t.substring(0, 45);

            // 구분자/개행 제거(혹시 모를 포맷 깨짐 방지)
            t = t.replace('\n', ' ').replace('\r', ' ').replace('|', ' ').trim();
            if (!t.isBlank()) out.add(t);

            if (out.size() == 3) break;
        }

        // 최소 2개 맞추기
        if (out.size() == 1) out.add("분위기/카테고리 적합");
        if (out.isEmpty()) out.addAll(List.of("요청 조건에 부합", "상황에 적합"));

        // 2~3개 유지
        if (out.size() > 3) return out.subList(0, 3);
        if (out.size() < 2) {
            List<String> padded = new ArrayList<>(out);
            padded.add("추가 조건에도 무난");
            return padded.subList(0, 2);
        }
        return out;
    }

    private List<Long> extractBarIds(String pool) {
        Matcher m = ID_PATTERN.matcher(pool);
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        while (m.find()) {
            ids.add(Long.parseLong(m.group(1)));
        }
        return new ArrayList<>(ids);
    }
}
