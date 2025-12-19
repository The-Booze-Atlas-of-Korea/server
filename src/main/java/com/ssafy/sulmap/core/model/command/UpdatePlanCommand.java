package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.core.model.enums.PlanTheme;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdatePlanCommand(Long planId,
        Long userId, // For ownership check
        String title,
        String description,
        PlanTheme theme,
        Long totalBudget,
        List<DrinkingPlanSpotModel> spots // Full replacement of spots
) {

}
