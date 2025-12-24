package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.*;

public record GetRecommendedBarsRequest(
        @NotNull @DecimalMin(value = "-90.0")  @DecimalMax(value = "90.0")  Double lat,
        @NotNull @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0") Double lon,
        @NotNull @Min(50) @Max(20000) Integer maxDistance,
        @NotBlank @Size(max = 300) String userPrompt
) {

}
