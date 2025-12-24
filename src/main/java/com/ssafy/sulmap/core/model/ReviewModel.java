package com.ssafy.sulmap.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 리뷰 도메인 모델
 */
@Getter
@Setter
@Builder
public class ReviewModel {
    private Long id;
    private Long userId;
    private Long barId;
    private Long visitId;
    private Integer rating;
    private String content;
    private List<ReviewMediaModel> media;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
