package com.ssafy.sulmap.core.model.enums;

import java.util.Arrays;

public enum ScheduleStatus {
    PLANNED,
    CANCELED,
    COMPLETED;

    public static ScheduleStatus fromString(String value){
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(PLANNED);
    }
}
