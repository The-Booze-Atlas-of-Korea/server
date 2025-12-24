package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import com.ssafy.sulmap.core.repository.PlanRepository;
import com.ssafy.sulmap.infra.mapper.PlanMapper;
import com.ssafy.sulmap.infra.model.DrinkingPlanEntity;
import com.ssafy.sulmap.infra.model.DrinkingPlanSpotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlanRepositoryImpl implements PlanRepository {

    private final PlanMapper _planMapper;

    @Override
    @Transactional
    public DrinkingPlanModel save(DrinkingPlanModel plan) {
        if (plan.getId() == null) {
            // 신규 플랜 생성
            return insertNewPlan(plan);
        } else {
            // 기존 플랜 수정
            return updateExistingPlan(plan);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DrinkingPlanModel> findById(Long id) {
        DrinkingPlanEntity planEntity = _planMapper.selectById(id);
        if (planEntity == null) {
            return Optional.empty();
        }

        List<DrinkingPlanSpotEntity> spotEntities = _planMapper.selectSpotsByPlanId(id);
        return Optional.of(planEntity.toModel(spotEntities));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkingPlanModel> findByOwnerUserId(Long ownerUserId) {
        List<DrinkingPlanEntity> entities = _planMapper.selectByOwnerUserId(ownerUserId);
        // spots 없는 목록 반환 - N+1 방지
        return entities.stream()
                .map(entity -> entity.toModel(List.of())) // 빈 spots 리스트
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 플랜에 속한 스팟 먼저 삭제
        _planMapper.deleteSpotsByPlanId(id);
        // 플랜 삭제
        _planMapper.deletePlan(id);
    }

    @Override
    public List<DrinkingPlanModel> findByOwnerUserId(Long ownerUserId, int offset, int limit, String sort) {
        var entities = _planMapper.selectByOwnerUserIdWithPaging(ownerUserId, offset, limit, sort);

        return entities.stream().map(entity -> {
            var planId = entity.getId();
            var spotEntities = _planMapper.selectSpotsByPlanId(planId);
            return entity.toModel(spotEntities);
        }).toList();
    }

    /**
     * 신규 플랜 삽입
     */
    private DrinkingPlanModel insertNewPlan(DrinkingPlanModel plan) {
        // 타임스탬프 설정
        LocalDateTime now = LocalDateTime.now();
        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);

        // 플랜 엔티티 변환 및 삽입
        DrinkingPlanEntity planEntity = DrinkingPlanEntity.fromModel(plan);
        _planMapper.insertPlan(planEntity);

        // 자동 생성된 ID를 플랜 모델에 설정
        plan.setId(planEntity.getId());

        // 스팟 삽입
        if (plan.getSpots() != null && !plan.getSpots().isEmpty()) {
            insertPlanSpots(plan.getId(), plan.getSpots());
        }

        return plan;
    }

    /**
     * 기존 플랜 수정 (스팟 전체 재구성)
     */
    private DrinkingPlanModel updateExistingPlan(DrinkingPlanModel plan) {
        // 타임스탬프 갱신
        plan.setUpdatedAt(LocalDateTime.now());

        // 플랜 정보 업데이트
        DrinkingPlanEntity planEntity = DrinkingPlanEntity.fromModel(plan);
        _planMapper.updatePlan(planEntity);

        // 기존 스팟 전체 삭제
        _planMapper.deleteSpotsByPlanId(plan.getId());

        // 새 스팟 삽입
        if (plan.getSpots() != null && !plan.getSpots().isEmpty()) {
            insertPlanSpots(plan.getId(), plan.getSpots());
        }

        return plan;
    }

    /**
     * 플랜 스팟 목록 삽입
     */
    private void insertPlanSpots(Long planId, List<com.ssafy.sulmap.core.model.DrinkingPlanSpotModel> spots) {
        for (com.ssafy.sulmap.core.model.DrinkingPlanSpotModel spot : spots) {
            DrinkingPlanSpotEntity spotEntity = DrinkingPlanSpotEntity.fromModel(spot, planId);
            _planMapper.insertPlanSpot(spotEntity);
            // 자동 생성된 ID를 스팟 모델에 설정
            spot.setId(spotEntity.getId());
            spot.setPlanId(planId);
        }
    }
}
