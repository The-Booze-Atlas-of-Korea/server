package com.ssafy.sulmap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BarListItemModel {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceMeters;
    private String menuJsonString;
    private String baseCategoryName;
    private String openInformation;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
