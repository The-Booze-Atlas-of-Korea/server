package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.repository.ScheduleRepository;
import com.ssafy.sulmap.infra.mapper.ScheduleMapper;
import com.ssafy.sulmap.infra.model.DrinkingScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleMapper _scheduleMapper;

    @Override
    @Transactional
    public DrinkingScheduleModel save(DrinkingScheduleModel schedule) {
        if (schedule.getId() == null) {
            // 신규 일정 생성
            return insertNewSchedule(schedule);
        } else {
            // 기존 일정 수정
            return updateExistingSchedule(schedule);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DrinkingScheduleModel> findById(Long id) {
        DrinkingScheduleEntity entity = _scheduleMapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(entity.toModel());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkingScheduleModel> findByPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        List<DrinkingScheduleEntity> entities = _scheduleMapper.selectByPeriod(userId, start, end);
        return entities.stream()
                .map(DrinkingScheduleEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkingScheduleModel> findByOwnerUserIdPaged(Long userId, int offset, int limit) {
        List<DrinkingScheduleEntity> entities = _scheduleMapper.selectByOwnerUserIdPaged(userId, offset, limit);
        return entities.stream()
                .map(DrinkingScheduleEntity::toModel)
                .toList();
    }

    /**
     * 신규 일정 삽입
     */
    private DrinkingScheduleModel insertNewSchedule(DrinkingScheduleModel schedule) {
        // 타임스탬프 설정
        LocalDateTime now = LocalDateTime.now();
        schedule.setCreatedAt(now);
        schedule.setUpdatedAt(now);

        // 엔티티 변환 및 삽입
        DrinkingScheduleEntity entity = DrinkingScheduleEntity.fromModel(schedule);
        _scheduleMapper.insert(entity);

        // 자동 생성된 ID를 모델에 설정
        schedule.setId(entity.getId());

        return schedule;
    }

    /**
     * 기존 일정 수정
     */
    private DrinkingScheduleModel updateExistingSchedule(DrinkingScheduleModel schedule) {
        // 타임스탬프 갱신
        schedule.setUpdatedAt(LocalDateTime.now());

        // 엔티티 변환 및 업데이트
        DrinkingScheduleEntity entity = DrinkingScheduleEntity.fromModel(schedule);
        _scheduleMapper.update(entity);

        return schedule;
    }
}
