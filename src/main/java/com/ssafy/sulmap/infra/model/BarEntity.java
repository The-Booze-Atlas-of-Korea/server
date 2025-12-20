package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.BarCategoryModel;
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
    ///     id                 BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    ///     name               VARCHAR(100)   NOT NULL,
    ///     address            VARCHAR(255)   NOT NULL,
    ///     latitude           DECIMAL(10, 7) NOT NULL,
    ///     longitude          DECIMAL(10, 7) NOT NULL,
    ///     base_category_name VARCHAR(50)    NULL,
    ///     open_information   TEXT           NULL,
    ///     menu               json           NULL,
    ///
    ///     created_at         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ///     updated_at         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ///     deleted_at         DATETIME       NULL,

    private long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String baseCategoryName;
    private String openInformation;
    private String menuJsonString;
    private Date createAt;
    private Date updateAt;
    private Date deleteAt;


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
                .createdAt(this.getCreateAt())
                .updatedAt(this.getUpdateAt())
                .deletedAt(this.getDeleteAt())
                .build();
    }
}
