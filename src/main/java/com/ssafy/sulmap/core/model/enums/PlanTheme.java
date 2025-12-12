package com.ssafy.sulmap.core.model.enums;

import java.util.Arrays;

public enum PlanTheme {
    DATE,
    COMPANY,
    FRIEND,
    ETC;

    public static PlanTheme fromString(String value){
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(ETC);
    }
}
