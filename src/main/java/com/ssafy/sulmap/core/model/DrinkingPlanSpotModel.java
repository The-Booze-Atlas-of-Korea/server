package com.ssafy.sulmap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrinkingPlanSpotModel {
    private Long id;
    private Long planId;

    // 위치 참조 (데이터베이스에 없는 사용자 지정 장소인 경우 null 가능)
    private Long placeId;

    // Snapshot data
    private String placeNameSnapshot;
    private String placeAddressSnapshot;

    private Double latitude;
    private Double longitude;

    private Integer sequence;
    private String memo;

}
