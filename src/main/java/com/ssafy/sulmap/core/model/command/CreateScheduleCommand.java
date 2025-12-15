package com.ssafy.sulmap.core.model.command;

import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import java.time.LocalDateTime;

public record CreateScheduleCommand(
        Long ownerUserId,
        Long planId, // Nullable
        String scheduleTitle,
        LocalDateTime meetAt) {
}

