package com.ssafy.sulmap.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 리뷰 요약 통계 모델
 */
@Getter
@Setter
@Builder
public class ReviewSummaryModel {
    private Long totalCount; // 전체 리뷰 개수
    private Double averageRating; // 평균 별점
    private Map<Integer, Long> ratingDistribution; // 별점별 분포 (1~5)
}
