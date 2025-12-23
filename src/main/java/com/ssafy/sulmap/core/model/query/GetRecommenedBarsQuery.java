package com.ssafy.sulmap.core.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRecommenedBarsQuery {
    private double lat;
    private double lon;
    private Long userId;
    private Integer maxDistance;
    private String weatherKey = "-";
    private String userPrompt;
}
