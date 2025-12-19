package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanSpotRequest(
        Long placeId, // nullable - 사용자가 직접 입력한 장소일 수 있음

        @NotBlank String placeNameSnapshot,

        String placeAddressSnapshot,

        Double latitude,

        Double longitude,

        @NotNull Integer sequence,

        String memo) {
}
