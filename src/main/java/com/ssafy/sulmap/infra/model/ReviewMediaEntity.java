package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.ReviewMediaModel;
import com.ssafy.sulmap.core.model.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 미디어 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMediaEntity {
    private Long id;
    private Long reviewId;
    private String mediaType; // DB에서는 문자열로 저장
    private String url;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 엔티티 → 모델 변환
     */
    public ReviewMediaModel toModel() {
        return ReviewMediaModel.builder()
                .id(id)
                .reviewId(reviewId)
                .mediaType(MediaType.valueOf(mediaType))
                .url(url)
                .orderIndex(orderIndex)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    /**
     * 모델 → 엔티티 변환
     */
    public static ReviewMediaEntity fromModel(ReviewMediaModel model, Long reviewId) {
        return ReviewMediaEntity.builder()
                .id(model.getId())
                .reviewId(reviewId)
                .mediaType(model.getMediaType() != null ? model.getMediaType().name() : null)
                .url(model.getUrl())
                .orderIndex(model.getOrderIndex())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
