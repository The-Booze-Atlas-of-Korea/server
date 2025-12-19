package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.core.model.enums.PlanTheme;
import lombok.Builder;

import java.util.List;

@Builder
public record CreatePlanCommand(
                Long ownerUserId,
                String title,
                String description,
                PlanTheme theme,
                Long totalBudget,
                List<DrinkingPlanSpotModel> spots) {
}
