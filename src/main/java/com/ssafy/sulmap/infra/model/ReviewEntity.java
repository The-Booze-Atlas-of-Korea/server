package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.ReviewModel;
import com.ssafy.sulmap.core.model.ReviewMediaModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEntity {
    private Long id;
    private Long userId;
    private Long barId;
    private Long visitId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * 엔티티 → 모델 변환
     */
    public ReviewModel toModel(List<ReviewMediaEntity> mediaEntities) {
        return ReviewModel.builder()
                .id(id)
                .userId(userId)
                .barId(barId)
                .visitId(visitId)
                .rating(rating)
                .content(content)
                .media(mediaEntities != null
                        ? mediaEntities.stream()
                                .map(ReviewMediaEntity::toModel)
                                .collect(Collectors.toList())
                        : List.of())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    /**
     * 모델 → 엔티티 변환
     */
    public static ReviewEntity fromModel(ReviewModel model) {
        return ReviewEntity.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .barId(model.getBarId())
                .visitId(model.getVisitId())
                .rating(model.getRating())
                .content(model.getContent())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .deletedAt(model.getDeletedAt())
                .build();
    }
}
