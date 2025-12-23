package com.ssafy.sulmap.api.dto.response;

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
@AllArgsConstructor
@NoArgsConstructor
public class PlanResponse {
    private Long id;
    private Long ownerUserId;
    private String title;
    private String description;
    private String theme;
    private Long totalBudget;
    private List<PlanSpotResponse> spots;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlanResponse fromModel(DrinkingPlanModel model) {
        return PlanResponse.builder()
                .id(model.getId())
                .ownerUserId(model.getOwnerUserId())
                .title(model.getTitle())
                .description(model.getDescription())
                .theme(model.getTheme())
                .totalBudget(model.getTotalBudget())
                .spots(model.getSpots() != null ? model.getSpots().stream()
                        .map(PlanSpotResponse::fromModel)
                        .collect(Collectors.toList()) : null)
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
