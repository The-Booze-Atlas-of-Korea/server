package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.share.result.Result;

public interface PlanService {
    Result<DrinkingPlanModel> createPlan(CreatePlanCommand command);

    Result<DrinkingPlanModel> updatePlan(UpdatePlanCommand command);

    Result<DrinkingPlanModel> getPlan(Long planId);
}
