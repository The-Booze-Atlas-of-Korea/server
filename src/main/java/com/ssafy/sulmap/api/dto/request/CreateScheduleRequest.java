package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateScheduleRequest(
        Long planId, // nullable - 플랜 없이 일정만 생성 가능

        @NotBlank String scheduleTitle,

        @NotNull LocalDateTime meetAt) {
}
