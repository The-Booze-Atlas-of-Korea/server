package com.ssafy.sulmap.core.model.command;

import java.time.LocalDateTime;

public record CreateScheduleCommand(
        Long ownerUserId,
        Long planId,
        String scheduleTitle,
        LocalDateTime meetAt
) {
}
