package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePlanRequest(
                @NotBlank String title,

                String description,

                @NotBlank @Size(max = 50) String theme, // Free-text, required, max 50 chars

                Long totalBudget,

                @Valid List<PlanSpotRequest> spots) { // Nullable
}
