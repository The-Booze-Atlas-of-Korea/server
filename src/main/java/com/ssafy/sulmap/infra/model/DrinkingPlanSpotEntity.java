package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrinkingPlanSpotEntity {
    private Long id;
    private Long planId;
    private Long placeId; // Nullable
    private String placeNameSnapshot;
    private String placeAddressSnapshot;
    private Double latitude;
    private Double longitude;
    private Integer sequence;
    private String memo;

    /**
     * Core Model을 Entity로 변환
     */
    public static DrinkingPlanSpotEntity fromModel(DrinkingPlanSpotModel model, Long planId) {
        return DrinkingPlanSpotEntity.builder()
                .id(model.getId())
                .planId(planId)
                .placeId(model.getPlaceId())
                .placeNameSnapshot(model.getPlaceNameSnapshot())
                .placeAddressSnapshot(model.getPlaceAddressSnapshot())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .sequence(model.getSequence())
                .memo(model.getMemo())
                .build();
    }

    /**
     * Entity를 Core Model로 변환
     */
    public DrinkingPlanSpotModel toModel() {
        return DrinkingPlanSpotModel.builder()
                .id(this.id)
                .planId(this.planId)
                .placeId(this.placeId)
                .placeNameSnapshot(this.placeNameSnapshot)
                .placeAddressSnapshot(this.placeAddressSnapshot)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .sequence(this.sequence)
                .memo(this.memo)
                .build();
    }
}
