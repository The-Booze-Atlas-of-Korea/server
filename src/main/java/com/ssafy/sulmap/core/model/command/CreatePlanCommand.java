package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.enums.PlanTheme;

public record CreatePlanCommand(
        Long ownerUserId,
        String title,
        String description,
        PlanTheme theme,
        Long totalBudget,
        List<DrinkingPlanSpotModel> spots) {
}

