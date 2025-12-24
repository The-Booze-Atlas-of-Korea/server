package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.DrinkingScheduleModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    DrinkingScheduleModel save(DrinkingScheduleModel schedule);

    Optional<DrinkingScheduleModel> findById(Long id);

    List<DrinkingScheduleModel> findByPeriod(Long userId, LocalDateTime start, LocalDateTime end);

    List<DrinkingScheduleModel> findByOwnerUserIdPaged(Long userId, int offset, int limit);
  
    void delete(Long id);
}
