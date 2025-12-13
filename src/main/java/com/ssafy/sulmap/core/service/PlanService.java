package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;

public interface PlanService {
    DrinkingPlanModel createPlan(CreatePlanCommand command);

    DrinkingPlanModel updatePlan(CreatePlanCommand command);

    DrinkingPlanModel getPlan(Long planId);
}
