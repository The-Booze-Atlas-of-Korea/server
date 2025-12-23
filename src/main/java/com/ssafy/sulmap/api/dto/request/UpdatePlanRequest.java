package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePlanRequest(
                String title,

                String description,

                @Size(max = 50) String theme, // null = no change

                Long totalBudget,

                @Valid List<PlanSpotRequest> spots) { // null = no change, [] = delete all
}
