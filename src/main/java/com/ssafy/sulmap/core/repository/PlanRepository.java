package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import java.util.List;
import java.util.Optional;

public interface PlanRepository {
    DrinkingPlanModel save(DrinkingPlanModel plan);

    Optional<DrinkingPlanModel> findById(Long id);

    List<DrinkingPlanModel> findByOwnerUserId(Long ownerUserId);

    void delete(Long id);
    List<DrinkingPlanModel> findByOwnerUserId(Long ownerUserId, int offset, int limit, String sort);
}
