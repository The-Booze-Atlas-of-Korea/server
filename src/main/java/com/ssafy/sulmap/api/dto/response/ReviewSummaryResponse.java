package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.ReviewSummaryModel;

import java.util.Map;

/**
 * 리뷰 요약 통계 응답 DTO
 */
public record ReviewSummaryResponse(
        Long totalCount,
        Double averageRating,
        Map<Integer, Long> ratingDistribution) {
    public static ReviewSummaryResponse fromModel(ReviewSummaryModel model) {
        return new ReviewSummaryResponse(
                model.getTotalCount(),
                model.getAverageRating(),
                model.getRatingDistribution());
    }
}
