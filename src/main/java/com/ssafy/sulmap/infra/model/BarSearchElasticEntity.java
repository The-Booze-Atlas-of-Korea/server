package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.BarListItemModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarSearchElasticEntity {
    private Long id;
    private String address;
    private String name;
    private String baseCategoryName;
    private Double latitude;
    private Double longitude;
    private String openInfo;
    private String menuJsonString;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Double distanceMeters;

    public BarListItemModel toBarListItemModel() {
        return BarListItemModel.builder()
                .id(this.getId())
                .address(this.getAddress())
                .name(this.getName())
                .latitude(this.getLatitude())
                .longitude(this.getLongitude())
                .baseCategoryName(this.getBaseCategoryName())
                .openInformation(this.getOpenInfo())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .deletedAt(this.getDeletedAt())
                .build();
    }
}
