package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.DrinkingPlanModel;
import java.util.Optional;

public interface PlanRepository {
    DrinkingPlanModel save(DrinkingPlanModel plan);
    Optional<DrinkingPlanModel> findById(Long id);
    void delete(Long id);
}
