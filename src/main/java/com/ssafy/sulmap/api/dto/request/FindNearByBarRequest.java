package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FindNearByBarRequest(
        @NotNull
        long user_id,

        @NotNull
        double latitude,

        @NotNull
        double longitude,

        @NotNull
        int radiusMeters,

        @NotNull
        int count,

        String keyword,        // optional: 상호/카테고리/태그 검색

        String category,       // optional: 주점/이자카야 등

        String sort // optional: distance|recommended|rating ...
) {


}