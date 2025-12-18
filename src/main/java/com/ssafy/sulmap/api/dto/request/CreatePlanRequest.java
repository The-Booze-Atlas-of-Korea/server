package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreatePlanRequest(
        @NotBlank String title,

        String description,

        @NotBlank String theme, // FRIEND, COMPANY, DATE, ETC

        Long totalBudget,

        @NotNull @Valid List<PlanSpotRequest> spots) {
}
