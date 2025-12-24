package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.RecommendedBarModel;

public record RecommendedBarItemResponse(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String baseCategoryName,
        String openInformation,
        int recommendRank,
        String recommendReason
) {
    public static RecommendedBarItemResponse fromModel(RecommendedBarModel m) {
        return new RecommendedBarItemResponse(
                m.getId(),
                m.getName(),
                m.getAddress(),
                m.getLatitude(),
                m.getLongitude(),
                m.getBaseCategoryName(),
                m.getOpenInformation(),
                m.getRecommendRank(),
                m.getRecommendReason()
        );
    }
}
