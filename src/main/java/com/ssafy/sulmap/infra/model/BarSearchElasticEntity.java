package com.ssafy.sulmap.infra.model;

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
}
