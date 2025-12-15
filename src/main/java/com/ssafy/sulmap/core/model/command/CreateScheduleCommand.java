package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import java.time.LocalDateTime;

public record CreateScheduleCommand(
        Long scheduleId,
        Long userId, // For ownership check
        String scheduleTitle,
        LocalDateTime meetAt,
        ScheduleStatus status
) {
}
