package com.ssafy.sulmap.core.model.query;

import java.time.LocalDateTime;

public record GetSchedulesInPeriodQuery (
        Long userId,
        LocalDateTime startDate,
        LocalDateTime endDate
){

}
