package com.ssafy.sulmap.core.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendedBarModel extends MinorRecommendedBarModel {
    private int recommendRank;
    private String recommendReason;
}
