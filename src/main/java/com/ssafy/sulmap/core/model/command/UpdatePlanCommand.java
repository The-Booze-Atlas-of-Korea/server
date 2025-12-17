package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.core.model.enums.PlanTheme;
import java.util.List;

public record UpdatePlanCommand (Long planId,
                                 Long userId, // For ownership check
                                 String title,
                                 String description,
                                 PlanTheme theme,
                                 Long totalBudget,
                                 List<DrinkingPlanSpotModel> spots // Full replacement of spots
) {

}
