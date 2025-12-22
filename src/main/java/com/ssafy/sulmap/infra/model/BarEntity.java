
package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.infra.utils.MenuJsonHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarEntity {
    private long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String baseCategoryName;
    private String openInformation;
    private String menuJsonString;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;


    public BarModel toBarModel(List<BarCategoryEntity> barCategoryEntityList) {
        var categoryModels = barCategoryEntityList.stream()
                .map(BarCategoryEntity::toBarCategoryModel)
                .toList();

        return BarModel.builder()
                .id(this.getId())
                .name(this.getName())
                .address(this.getAddress())
                .latitude(this.getLatitude())
                .longitude(this.getLongitude())
                .baseCategoryName(this.getBaseCategoryName())
                .open_information(this.getOpenInformation())
                .menus(MenuJsonHelper.parseMenuJsonString(this.getMenuJsonString()))
                .categories(categoryModels)
                .open_information(this.getOpenInformation())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .deletedAt(this.getDeletedAt())
                .build();
    }
}
