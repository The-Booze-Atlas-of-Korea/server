package com.ssafy.sulmap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BarListItemModel {
    private Long id;
    private String name;
    private String Address;
    private Double latitude;
    private Double longitude;
    private String baseCategoryName;
    private String open_information;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
