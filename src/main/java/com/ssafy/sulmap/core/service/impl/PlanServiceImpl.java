package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.repository.PlanRepository;
import com.ssafy.sulmap.core.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;

    @Override
    @Transactional
    public DrinkingPlanModel createPlan(CreatePlanCommand command) {
        return null;
    }

    @Override
    public DrinkingPlanModel updatePlan(CreatePlanCommand command) {
        return null;
    }

    @Override
    public DrinkingPlanModel getPlan(Long planId) {
        return null;
    }
}
