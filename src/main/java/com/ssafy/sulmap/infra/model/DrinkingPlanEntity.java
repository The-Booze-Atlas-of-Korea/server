package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.enums.PlanTheme;
import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrinkingPlanEntity {
    private Long id;
    private Long ownerUserId;
    private String title;
    private String description;
    private String theme; // DB에는 String으로 저장
    private Long totalBudget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Core Model을 Entity로 변환
     */
    public static DrinkingPlanEntity fromModel(DrinkingPlanModel model) {
        return DrinkingPlanEntity.builder()
                .id(model.getId())
                .ownerUserId(model.getOwnerUserId())
                .title(model.getTitle())
                .description(model.getDescription())
                .theme(model.getTheme() != null ? model.getTheme().name() : null)
                .totalBudget(model.getTotalBudget())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    /**
     * Entity를 Core Model로 변환
     */
    public DrinkingPlanModel toModel(List<DrinkingPlanSpotEntity> spots) {
        return DrinkingPlanModel.builder()
                .id(this.id)
                .ownerUserId(this.ownerUserId)
                .title(this.title)
                .description(this.description)
                .theme(this.theme != null ? PlanTheme.fromString(this.theme) : null)
                .totalBudget(this.totalBudget)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .spots(spots != null ? spots.stream()
                        .map(DrinkingPlanSpotEntity::toModel)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}
