package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UpdateScheduleCommand(
                Long scheduleId,
                Long userId, // For ownership check
                String scheduleTitle,
                LocalDateTime meetAt,
                ScheduleStatus status) {
}
