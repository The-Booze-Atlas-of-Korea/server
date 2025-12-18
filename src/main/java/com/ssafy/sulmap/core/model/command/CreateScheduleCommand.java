package com.ssafy.sulmap.core.model.command;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateScheduleCommand(
                Long ownerUserId,
                Long planId, // Nullable
                String scheduleTitle,
                LocalDateTime meetAt) {
}