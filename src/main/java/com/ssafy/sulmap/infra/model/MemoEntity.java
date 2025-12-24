package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.MemoModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 메모 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoEntity {
    private Long id;
    private Long userId;
    private Long barId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /**
     * 엔티티 → 모델 변환
     */
    public MemoModel toModel() {
        return MemoModel.builder()
                .id(id)
                .userId(userId)
                .barId(barId)
                .content(content)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    /**
     * 모델 → 엔티티 변환
     */
    public static MemoEntity fromModel(MemoModel model) {
        return MemoEntity.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .barId(model.getBarId())
                .content(model.getContent())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .deletedAt(model.getDeletedAt())
                .build();
    }
}
