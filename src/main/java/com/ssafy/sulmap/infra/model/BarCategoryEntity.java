package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.BarCategoryModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarCategoryEntity {
    ///     id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    ///     name       VARCHAR(50) NOT NULL,
    ///     group_name VARCHAR(50) NULL,
    ///
    ///     created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ///     updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ///     deleted_at DATETIME NULL,
    private String name;
    private String groupName;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
    private Integer priority;

    public BarCategoryModel toBarCategoryModel() {
        return BarCategoryModel.builder()
                .name(this.getName())
                .groupName(this.getGroupName())
                .priority(this.getPriority())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .deletedAt(this.getDeletedAt())
                .build();
    }
}
