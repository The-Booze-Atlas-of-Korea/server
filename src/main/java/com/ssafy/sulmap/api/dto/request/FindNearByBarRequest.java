package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.*;

public record FindNearByBarRequest(
        @NotNull
        double latitude,

        @NotNull
        double longitude,

        @NotNull
        int radiusMeters,

        @NotNull
        @Min(1) @Max(50)
        int count,

        String keyword,        // optional: 상호/카테고리/태그 검색

        String category,       // optional: 주점/이자카야 등

        @Pattern(regexp = "^(distance|recommended|rating)?$", message = "sort must be one of distance|recommended|rating")
        String sort // optional: distance|recommended|rating ...
) {


}