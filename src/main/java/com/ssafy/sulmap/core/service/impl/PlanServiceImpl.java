package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.core.repository.PlanRepository;
import com.ssafy.sulmap.core.service.PlanService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository _planRepository;

    @Override
    @Transactional
    public DrinkingPlanModel createPlan(CreatePlanCommand command) {
        DrinkingPlanModel plan = DrinkingPlanModel.builder()
                .ownerUserId(command.ownerUserId())
                .title(command.title())
                .description(command.description())
                .theme(command.theme())
                .totalBudget(command.totalBudget())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .spots(command.spots())
                .build();
        return _planRepository.save(plan);
    }

    @Override
    @Transactional
    public DrinkingPlanModel updatePlan(UpdatePlanCommand command) {
        DrinkingPlanModel plan = _planRepository.findById(command.planId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        if (!plan.getOwnerUserId().equals(command.userId())) {
            throw new IllegalStateException("Only owner can update the plan");
        }

        plan.update(command.title(), command.description(), command.theme(), command.totalBudget());
        plan.setSpots(command.spots()); // Full replacement of spots

        return _planRepository.save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkingPlanModel getPlan(Long planId) {
        return _planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
    }
}
