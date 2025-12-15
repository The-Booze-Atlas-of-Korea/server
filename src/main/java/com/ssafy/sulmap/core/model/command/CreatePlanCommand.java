package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.core.model.enums.PlanTheme;
import java.util.List;

public record CreatePlanCommand(
        Long ownerUserId,
        String title,
        String description,
        PlanTheme theme,
        Long totalBudget,
        List<DrinkingPlanSpotModel> spots) {
}

