package com.ssafy.sulmap.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BarCategoryModel {
    private Long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
