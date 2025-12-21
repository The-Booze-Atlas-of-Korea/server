package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.core.repository.PlanRepository;
import com.ssafy.sulmap.core.service.PlanService;
import com.ssafy.sulmap.share.result.Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
                .spots(command.spots())
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

                    // 플랜 업데이트
                    plan.update(command.title(), command.description(), command.theme(), command.totalBudget());
                    plan.setSpots(command.spots());

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
    public Result<List<DrinkingPlanModel>> listPlans(Long ownerUserId, int page, int size, String sort) {
        // 파라미터 방어
        if (ownerUserId == null) return Result.fail(401, "인증이 필요합니다");
        if (page < 0) return Result.fail(400, "page는 0 이상이어야 합니다");
        if (size <= 0) return Result.fail(400, "size는 1 이상이어야 합니다");

        // 과도한 요청 방지(선택)
        if (size > 100) {
            return Result.fail(400, "size는 100 이하여야 합니다.");
        }


        // sort는 아직 미확정이면 무시해도 됨(일단 파라미터만 받기)
        // offset/limit 계산 (page=0부터)
        int offset = page * size;
        int limit = size;

        //인젝션 방어
        String normalizedSort = normalizeSortOrDefault(sort);

        // Repository 메서드가 필요함 (아래 3) 참고)
        List<DrinkingPlanModel> plans = _planRepository.findByOwnerUserId(ownerUserId, offset, limit, sort);

        return Result.ok(plans);
    }
    private String normalizeSortOrDefault(String sort) {
        if (sort == null || sort.isBlank())
            return "createdAt,desc";

        // 허용 리스트(필요 시 확장)
        return switch (sort) {
            case "createdAt,desc", "createdAt,asc" -> sort;
            default -> "createdAt,desc";
        };
    }
}
