package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.MinorRecommendedBarModel;
import com.ssafy.sulmap.core.model.RecommendedBarModel;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.query.GetRecommenedBarsQuery;
import com.ssafy.sulmap.core.repository.AiRecommendRepository;
import com.ssafy.sulmap.infra.external.openai.GptMinorRecommendClient;
import com.ssafy.sulmap.infra.external.openai.GptRecommendClient;
import com.ssafy.sulmap.infra.utils.GptBatchTextBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Repository
@RequiredArgsConstructor
@Slf4j
public class AiRecommendRepositoryImpl implements AiRecommendRepository {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final GptMinorRecommendClient _gptMinorRecommendClient;
    private final GptRecommendClient _gptRecommendClient;

    @Override
    public List<MinorRecommendedBarModel> getMinorRecommend(
            List<BarListItemModel> models,
            UserModel user,
            GetRecommenedBarsQuery query,
            int topK
    ) {
        if (models == null || models.isEmpty() || topK <= 0) return List.of();

        // 후보 id 인덱스
        Map<Long, BarListItemModel> byId = indexById(models);
        if (byId.isEmpty()) return List.of();

        String ctx = GptBatchTextBuilder.buildCtx(user, query, ZonedDateTime.now(ZONE));
        String batch = GptBatchTextBuilder.buildBatchLines(models);

        List<Long> selectedIds;
        try {
            var out = _gptMinorRecommendClient.rank(topK, ctx, batch); // { selected: [id,...] }
            selectedIds = (out == null || out.selected == null) ? List.of() : out.selected;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // AI 실패 폴백: 입력 순서대로 topK
            selectedIds = models.stream()
                    .map(BarListItemModel::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .limit(topK)
                    .toList();
        }

        // 후보 밖/중복 제거 + 순서 유지
        LinkedHashSet<Long> uniq = new LinkedHashSet<>();
        for (Long id : selectedIds) {
            if (id == null) continue;
            if (!byId.containsKey(id)) continue;
            uniq.add(id);
            if (uniq.size() >= topK) break;
        }

        // 부족하면 후보 순서대로 채우기
        if (uniq.size() < Math.min(topK, byId.size())) {
            for (BarListItemModel b : models) {
                if (uniq.size() >= topK) break;
                if (b == null || b.getId() == null) continue;
                uniq.add(b.getId());
            }
        }

        // MinorRecommendedBarModel로 변환(BarListItemModel 필드 복사)
        List<MinorRecommendedBarModel> result = new ArrayList<>(Math.min(topK, uniq.size()));
        for (Long id : uniq) {
            if (result.size() >= topK) break;
            BarListItemModel src = byId.get(id);
            if (src == null) continue;
            result.add(copyToMinor(src));
        }

        return result;
    }

    @Override
    public List<RecommendedBarModel> getRecommend(
            List<BarListItemModel> models,
            UserModel user,
            GetRecommenedBarsQuery query,
            int topK
    ) {
        if (models == null || models.isEmpty() || topK <= 0) return List.of();

        Map<Long, BarListItemModel> byId = indexById(models);
        if (byId.isEmpty()) return List.of();

        String ctx = GptBatchTextBuilder.buildCtx(user, query, ZonedDateTime.now(ZONE));
        String pool = GptBatchTextBuilder.buildBatchLines(models);

        List<GptRecommendClient.Item> picked;
        try {
            var out = _gptRecommendClient.rankTop(topK, ctx, pool); // { top: [{barId, reasons[]}, ...] }
            picked = (out == null || out.top == null) ? List.of() : out.top;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // AI 실패 폴백: 입력 순서대로 topK
            List<RecommendedBarModel> fallback = new ArrayList<>();
            int rank = 1;
            for (BarListItemModel b : models) {
                if (fallback.size() >= topK) break;
                if (b == null || b.getId() == null) continue;
                fallback.add(copyToFinal(b, rank++, "fallback:ai_fail"));
            }
            return fallback;
        }

        // 후보 밖/중복 제거 + 순서 유지 + topK 강제
        LinkedHashMap<Long, String> ordered = new LinkedHashMap<>();
        for (var it : picked) {
            if (it == null) continue;
            long id = it.barId;
            if (!byId.containsKey(id)) continue;
            if (ordered.containsKey(id)) continue;

            String reason = joinReasons(it.reasons);
            ordered.put(id, reason);
            if (ordered.size() >= topK) break;
        }

        // 부족하면 후보 순서대로 채우기
        if (ordered.size() < Math.min(topK, byId.size())) {
            for (BarListItemModel b : models) {
                if (ordered.size() >= topK) break;
                if (b == null || b.getId() == null) continue;
                ordered.putIfAbsent(b.getId(), "후보 내 상위 / 조건 무난");
            }
        }

        // RecommendedBarModel 생성(rank + reason)
        List<RecommendedBarModel> result = new ArrayList<>(Math.min(topK, ordered.size()));
        int rank = 1;
        for (Map.Entry<Long, String> e : ordered.entrySet()) {
            if (result.size() >= topK) break;
            BarListItemModel src = byId.get(e.getKey());
            if (src == null) continue;

            result.add(copyToFinal(src, rank++, e.getValue()));
        }

        return result;
    }

    // -------------------------
    // Helpers
    // -------------------------

    private Map<Long, BarListItemModel> indexById(List<BarListItemModel> models) {
        return models.stream()
                .filter(Objects::nonNull)
                .filter(m -> m.getId() != null)
                .collect(Collectors.toMap(
                        BarListItemModel::getId,
                        m -> m,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private MinorRecommendedBarModel copyToMinor(BarListItemModel src) {
        // 상속 모델이므로 builder로 필드 복사
        return MinorRecommendedBarModel.builder()
                .id(src.getId())
                .name(src.getName())
                .address(src.getAddress())
                .latitude(src.getLatitude())
                .longitude(src.getLongitude())
                .baseCategoryName(src.getBaseCategoryName())
                .openInformation(src.getOpenInformation())
                .createdAt(src.getCreatedAt())
                .updatedAt(src.getUpdatedAt())
                .deletedAt(src.getDeletedAt())
                .build();
    }

    private RecommendedBarModel copyToFinal(BarListItemModel src, int rank, String reason) {
        return RecommendedBarModel.builder()
                // 상속 필드(BarListItemModel)
                .id(src.getId())
                .name(src.getName())
                .address(src.getAddress())
                .latitude(src.getLatitude())
                .longitude(src.getLongitude())
                .baseCategoryName(src.getBaseCategoryName())
                .openInformation(src.getOpenInformation())
                .createdAt(src.getCreatedAt())
                .updatedAt(src.getUpdatedAt())
                .deletedAt(src.getDeletedAt())
                // 추가 필드
                .recommendRank(rank)
                .recommendReason(reason)
                .build();
    }

    private String joinReasons(List<String> reasons) {
        if (reasons == null) return "요청 조건에 부합 / 상황에 적합";
        List<String> cleaned = reasons.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> sanitize(s, 45))
                .limit(3)
                .toList();

        if (cleaned.isEmpty()) return "요청 조건에 부합 / 상황에 적합";
        if (cleaned.size() == 1) return cleaned.get(0);
        return String.join(" / ", cleaned);
    }

    private String sanitize(String s, int maxLen) {
        String out = s.replace('\n', ' ').replace('\r', ' ').replace('|', ' ').trim();
        return out.length() <= maxLen ? out : out.substring(0, maxLen);
    }
}
