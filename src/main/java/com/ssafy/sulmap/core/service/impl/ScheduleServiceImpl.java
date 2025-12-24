package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.command.UpdateScheduleCommand;
import com.ssafy.sulmap.core.model.query.GetSchedulesInPeriodQuery;
import com.ssafy.sulmap.core.repository.ScheduleRepository;
import com.ssafy.sulmap.core.service.ScheduleService;
import com.ssafy.sulmap.share.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository _scheduleRepository;

    @Override
    public Result<DrinkingScheduleModel> createSchedule(CreateScheduleCommand command) {
        DrinkingScheduleModel schedule = DrinkingScheduleModel.builder()
                .ownerUserId(command.ownerUserId())
                .planId(command.planId())
                .scheduleTitle(command.scheduleTitle())
                .meetAt(command.meetAt())
                .status(ScheduleStatus.PLANNED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        DrinkingScheduleModel savedSchedule = _scheduleRepository.save(schedule);
        return Result.ok(savedSchedule);
    }

    @Override
    public Result<DrinkingScheduleModel> updateSchedule(UpdateScheduleCommand command) {
        return _scheduleRepository.findById(command.scheduleId())
                .map(schedule -> {
                    // 소유권 확인
                    if (!schedule.getOwnerUserId().equals(command.userId())) {
                        return Result.<DrinkingScheduleModel>fail(403, "일정 수정 권한이 없습니다");
                    }

                    // 일정 업데이트
                    schedule.reschedule(command.meetAt(), command.scheduleTitle());
                    if (command.status() != null) {
                        schedule.changeStatus(command.status());
                    }

                    DrinkingScheduleModel updatedSchedule = _scheduleRepository.save(schedule);
                    return Result.ok(updatedSchedule);
                })
                .orElse(Result.fail(404, "일정을 찾을 수 없습니다"));
    }

    @Override
    public Result<DrinkingScheduleModel> getSchedule(Long scheduleId) {
        return _scheduleRepository.findById(scheduleId)
                .map(Result::ok)
                .orElse(Result.fail(404, "일정을 찾을 수 없습니다"));
    }

    @Override
    public Result<List<DrinkingScheduleModel>> getSchedulesInPeriod(GetSchedulesInPeriodQuery query) {
        List<DrinkingScheduleModel> schedules = _scheduleRepository.findByPeriod(
                query.userId(),
                query.startDate(),
                query.endDate());
        return Result.ok(schedules);
    }

    @Override
    public Result<List<DrinkingScheduleModel>> getScheduleHistory(Long userId, int page, int size) {
        // 파라미터 검증
        if (page < 0) {
            return Result.fail(400, "페이지 번호는 0 이상이어야 합니다");
        }
        if (size <= 0 || size > 100) {
            return Result.fail(400, "페이지 크기는 1~100 사이여야 합니다");
        }

        int offset = page * size;
        List<DrinkingScheduleModel> schedules = _scheduleRepository.findByOwnerUserIdPaged(userId, offset, size);
        return Result.ok(schedules);
    }
  
    public Result<Void> deleteSchedule(Long scheduleId, Long userId) {
        return _scheduleRepository.findById(scheduleId)
                .map(schedule -> {
                    // 소유권 확인
                    if (!schedule.getOwnerUserId().equals(userId)) {
                        return Result.<Void>fail(403, "일정 삭제 권한이 없습니다");
                    }

                    _scheduleRepository.delete(scheduleId);
                    return Result.<Void>ok(null);
                })
                .orElse(Result.fail(404, "일정을 찾을 수 없습니다"));
    }
}