package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;

public interface PlanService {
    DrinkingPlanModel createPlan(CreatePlanCommand command);

    DrinkingPlanModel updatePlan(UpdatePlanCommand command);

    DrinkingPlanModel getPlan(Long planId);
}
