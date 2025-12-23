package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.core.repository.PlanRepository;
import com.ssafy.sulmap.core.service.PlanService;
import com.ssafy.sulmap.share.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository _planRepository;

    @Override
    public Result<DrinkingPlanModel> createPlan(CreatePlanCommand command) {
        DrinkingPlanModel plan = DrinkingPlanModel.builder()
                .ownerUserId(command.ownerUserId())
                .title(command.title())
                .description(command.description())
                .theme(command.theme())
                .totalBudget(command.totalBudget())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .spots(new ArrayList<>(command.spots()))
                .build();

        DrinkingPlanModel savedPlan = _planRepository.save(plan);
        return Result.ok(savedPlan);
    }

    @Override
    public Result<DrinkingPlanModel> updatePlan(UpdatePlanCommand command) {
        return _planRepository.findById(command.planId())
                .map(plan -> {
                    // 소유권 확인
                    if (!plan.getOwnerUserId().equals(command.userId())) {
                        return Result.<DrinkingPlanModel>fail(403, "플랜 수정 권한이 없습니다");
                    }

                    // 플랜 업데이트 (null = no change)
                    plan.update(
                            command.title() != null ? command.title() : plan.getTitle(),
                            command.description() != null ? command.description() : plan.getDescription(),
                            command.theme() != null ? command.theme() : plan.getTheme(),
                            command.totalBudget() != null ? command.totalBudget() : plan.getTotalBudget());

                    // spots: null = no change, non-null = sync (delete+insert in Repo)
                    if (command.spots() != null) {
                        plan.setSpots(new ArrayList<>(command.spots()));
                    }

                    DrinkingPlanModel updatedPlan = _planRepository.save(plan);
                    return Result.ok(updatedPlan);
                })
                .orElse(Result.fail(404, "플랜을 찾을 수 없습니다"));
    }

    @Override
    public Result<DrinkingPlanModel> getPlan(Long planId) {
        return _planRepository.findById(planId)
                .map(Result::ok)
                .orElse(Result.fail(404, "플랜을 찾을 수 없습니다"));
    }

    @Override
    public Result<List<DrinkingPlanModel>> listPlans(Long userId) {
        List<DrinkingPlanModel> plans = _planRepository.findByOwnerUserId(userId);
        return Result.ok(plans);
    }
}
