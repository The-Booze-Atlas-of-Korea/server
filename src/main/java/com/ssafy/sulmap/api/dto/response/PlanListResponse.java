package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.enums.PlanTheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 플랜 목록 조회용 응답 DTO (요약 정보)
 * GET /api/plans 전용
 * spots 필드 없음 - 상세 조회는 PlanResponse 사용
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanListResponse {
    private Long id;
    private Long ownerUserId;
    private String title;
    private String description;
    private PlanTheme theme;
    private Long totalBudget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlanListResponse fromModel(DrinkingPlanModel model) {
        return PlanListResponse.builder()
                .id(model.getId())
                .ownerUserId(model.getOwnerUserId())
                .title(model.getTitle())
                .description(model.getDescription())
                .theme(model.getTheme())
                .totalBudget(model.getTotalBudget())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
