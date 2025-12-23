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
public class GptMinorRecommendClient {

    private static final String MODEL = "gpt-5-nano";
    private static final double TEMPERATURE = 0.2;
    private static final long MAX_OUTPUT_TOKENS = 120;

    // batch 라인 포맷: "B|id=123|..."
    private static final Pattern BATCH_ID_PATTERN = Pattern.compile("(?m)^B\\|id=(\\d+)\\b");

    private final OpenAIClient _OpenAIClient;

    /**
     * 1차(배치) 토너먼트: 후보(batch) 안에서 topK개 barId만 선택
     */
    public MinorRankerOutput rank(int topK, String ctx, String batch) {
        if (topK <= 0) throw new IllegalArgumentException("topK must be positive");
        Objects.requireNonNull(ctx, "ctx");
        Objects.requireNonNull(batch, "batch");

        List<Long> allowed = extractBarIds(batch);
        if (allowed.isEmpty()) {
            throw new IllegalArgumentException("No barIds found in batch. Expected lines like: B|id=123|...");
        }

        String input = stageInstructions(topK, ctx, batch);

        StructuredResponseCreateParams<MinorRankerOutput> params = ResponseCreateParams.builder()
                .model(MODEL)
                .instructions(systemInstructions())
                .input(input)
                .temperature(TEMPERATURE)
                .maxOutputTokens(MAX_OUTPUT_TOKENS)
                .text(MinorRankerOutput.class)
                .build();

        var response = _OpenAIClient.responses().create(params);

        MinorRankerOutput out = response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(msg -> msg.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No structured output returned"));

        return normalize(out, topK, allowed);
    }

    // ===== Prompt =====

    private String stageInstructions(int topK, String ctx, String batch) {
        return String.join("\n",
                "아래 CTX와 후보(B 라인)만 보고, 이 배치에서 Top " + topK + "의 barId를 선택해라.",
                "",
                "[INPUT]",
                ctx,
                batch,
                "",
                "[OUTPUT JSON]",
                "{ \"selected\": [123, 456, 789] }",
                "",
                "제약:",
                "- selected는 정확히 " + topK + "개",
                "- barId 중복 금지",
                "- 반드시 B 라인에 존재하는 barId만 선택",
                "- JSON만 출력(설명/마크다운 금지)",
                "- selected의 순서는 추천 순위(앞이 더 추천)"
        );
    }

    /**
     * ✅ 여기서 “외부 지식/상식으로 보완”을 허용.
     * 단, 후보 밖 생성 금지/후보 라인 기반 판단 우선은 유지.
     */
    private String systemInstructions() {
        return String.join("\n",
                "너는 \"후보 축소\" 전용 랭커다.",
                "반드시 입력으로 주어진 B 라인(후보) 안에서만 선택한다.",
                "후보에 없는 술집을 만들거나 추측하지 마라. (새 barId 생성 금지)",
                "일반 상식/배경지식/추론은 보완적으로 활용 가능하다.",
                "단, 최종 선택은 CTX와 B 라인에 주어진 정보가 우선이며, B 라인과 모순되는 가정은 하지 마라.",
                "출력은 반드시 JSON만. 마크다운/코드펜스/설명 문장 금지.",
                "우선순위(동점 처리 포함):",
                "1) CTX.q(사용자 요청)과 B의 tg/c가 잘 맞는가",
                "2) o=1 우선",
                "3) 날씨(w)가 나쁠수록(비/눈/추위/강풍) d가 짧을수록 우선",
                "4) rt 높음, rc 많음 순",
                "힌트:",
                "- 사용 가능한 후보 정보는 B 라인의 c(카테고리), oi(영업정보요약), n(이름요약), menu(메뉴) 뿐이다.",
                "- CTX의 q(요청), w(날씨), ts(시간)를 반영해 취향/상황 적합도를 판단하라."
        );
    }

    // ===== Structured Output schema =====

    public static final class MinorRankerOutput {
        @JsonPropertyDescription("Top-K barIds in ranked order.")
        public List<Long> selected;

        public MinorRankerOutput() {}
        public MinorRankerOutput(List<Long> selected) { this.selected = selected; }
    }

    // ===== Normalization / Validation =====

    private MinorRankerOutput normalize(MinorRankerOutput out, int topK, List<Long> allowedInOrder) {
        Set<Long> allowedSet = new HashSet<>(allowedInOrder);

        // 1) 후보 밖 제거 + 중복 제거 + 순서 유지
        LinkedHashSet<Long> uniq = new LinkedHashSet<>();
        List<Long> selected = (out == null || out.selected == null) ? List.of() : out.selected;

        for (Long id : selected) {
            if (id == null) continue;
            if (!allowedSet.contains(id)) continue;
            uniq.add(id);
            if (uniq.size() >= topK) break;
        }

        // 2) 부족하면 배치 순서대로 채우기(폴백)
        for (Long id : allowedInOrder) {
            if (uniq.size() >= topK) break;
            uniq.add(id);
        }

        // 3) topK로 자르기
        List<Long> normalized = new ArrayList<>(topK);
        int count = 0;
        for (Long id : uniq) {
            normalized.add(id);
            count++;
            if (count >= topK) break;
        }

        return new MinorRankerOutput(normalized);
    }

    private List<Long> extractBarIds(String batch) {
        Matcher m = BATCH_ID_PATTERN.matcher(batch);
        LinkedHashSet<Long> ids = new LinkedHashSet<>();
        while (m.find()) {
            ids.add(Long.parseLong(m.group(1)));
        }
        return new ArrayList<>(ids);
    }
}
