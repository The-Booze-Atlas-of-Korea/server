package com.ssafy.sulmap.core.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class RecommendedBarModel extends MinorRecommendedBarModel {
    private int recommendRank;
    private String recommendReason;
}
