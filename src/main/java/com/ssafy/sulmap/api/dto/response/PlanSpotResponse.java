package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanSpotResponse {
    private Long id;
    private Long planId;
    private Long placeId;
    private String placeNameSnapshot;
    private String placeAddressSnapshot;
    private Double latitude;
    private Double longitude;
    private Integer sequence;
    private String memo;

    public static PlanSpotResponse fromModel(DrinkingPlanSpotModel model) {
        return PlanSpotResponse.builder()
                .id(model.getId())
                .planId(model.getPlanId())
                .placeId(model.getPlaceId())
                .placeNameSnapshot(model.getPlaceNameSnapshot())
                .placeAddressSnapshot(model.getPlaceAddressSnapshot())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .sequence(model.getSequence())
                .memo(model.getMemo())
                .build();
    }
}
