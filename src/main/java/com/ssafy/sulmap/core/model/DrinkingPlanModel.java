package com.ssafy.sulmap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrinkingPlanModel {
    private Long id;
    private Long ownerUserId;
    private String title;
    private String description;
    private String theme;
    private Long totalBudget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 선택 사항: 장소 목록은 한꺼번에 또는 개별적으로 로드할 수 있음.
    private List<DrinkingPlanSpotModel> spots = new ArrayList<>();

    public void update(String title, String description, String theme, Long totalBudget) {
        this.title = title;
        this.description = description;
        this.theme = theme;
        this.totalBudget = totalBudget;
        this.updatedAt = LocalDateTime.now();
    }
}
