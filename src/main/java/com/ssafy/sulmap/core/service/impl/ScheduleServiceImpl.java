package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.DrinkingScheduleModel;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.command.UpdateScheduleCommand;
import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.query.GetSchedulesInPeriodQuery;
import com.ssafy.sulmap.core.repository.ScheduleRepository;
import com.ssafy.sulmap.core.service.ScheduleService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    @Override
    @Transactional
    public DrinkingScheduleModel createSchedule(CreateScheduleCommand command) {
        DrinkingScheduleModel schedule = DrinkingScheduleModel.builder()
                .ownerUserId(command.ownerUserId())
                .planId(command.planId())
                .scheduleTitle(command.scheduleTitle())
                .meetAt(command.meetAt())
                .status(ScheduleStatus.PLANNED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public DrinkingScheduleModel updateSchedule(UpdateScheduleCommand command) {
        DrinkingScheduleModel schedule = scheduleRepository.findById(command.scheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        if (!schedule.getOwnerUserId().equals(command.userId())) {
            throw new IllegalStateException("Only owner can update the schedule");
        }

        schedule.reschedule(command.meetAt(), command.scheduleTitle());
        if (command.status() != null) {
            schedule.changeStatus(command.status());
        }

        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkingScheduleModel getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkingScheduleModel> getSchedulesInPeriod(GetSchedulesInPeriodQuery query) {
        return scheduleRepository.findByPeriod(query.userId(), query.startDate(), query.endDate());
    }
}
