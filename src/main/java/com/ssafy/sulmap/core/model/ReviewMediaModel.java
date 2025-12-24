package com.ssafy.sulmap.core.model;

import com.ssafy.sulmap.core.model.enums.MediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 리뷰 미디어 도메인 모델
 */
@Getter
@Setter
@Builder
public class ReviewMediaModel {
    private Long id;
    private Long reviewId;
    private MediaType mediaType;
    private String url;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
