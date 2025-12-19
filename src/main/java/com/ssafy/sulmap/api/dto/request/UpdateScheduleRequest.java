package com.ssafy.sulmap.api.dto.request;

import java.time.LocalDateTime;

public record UpdateScheduleRequest(
        String scheduleTitle,

        LocalDateTime meetAt,

        String status // PLANNED, CANCELED, COMPLETED
) {
}
