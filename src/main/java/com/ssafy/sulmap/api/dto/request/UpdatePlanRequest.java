package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.Valid;

import java.util.List;

public record UpdatePlanRequest(
        String title,

        String description,

        String theme, // FRIEND, COMPANY, DATE, ETC

        Long totalBudget,

        @Valid List<PlanSpotRequest> spots) {
}
