package com.ssafy.sulmap.core.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class MinorRecommendedBarModel extends BarListItemModel {
}
