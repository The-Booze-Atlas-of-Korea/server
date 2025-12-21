package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.share.result.Result;
import java.util.List;

public interface PlanService {
    Result<DrinkingPlanModel> createPlan(CreatePlanCommand command);

    Result<DrinkingPlanModel> updatePlan(UpdatePlanCommand command);

    Result<DrinkingPlanModel> getPlan(Long planId);

    Result<List<DrinkingPlanModel>> listPlans(Long ownerUserId, int page, int size, String sort);
}
