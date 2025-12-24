package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import lombok.Builder;

import java.util.List;

@Builder
public record CreatePlanCommand(
        Long ownerUserId,
        String title,
        String description,
        String theme,
        Long totalBudget,
        List<DrinkingPlanSpotModel> spots) {
}
