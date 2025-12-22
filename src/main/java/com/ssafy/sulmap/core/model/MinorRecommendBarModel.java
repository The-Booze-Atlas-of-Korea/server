package com.ssafy.sulmap.core.model;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinorRecommendBarModel {
    private Long id;
    private String name;
    private Double distanceMeters;
    private String baseCategoryName;
    private String shortReview;
    private String openInformation;
}
