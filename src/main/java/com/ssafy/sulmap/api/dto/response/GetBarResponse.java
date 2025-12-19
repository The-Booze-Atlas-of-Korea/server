package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.BarCategoryModel;
import com.ssafy.sulmap.core.model.MemoModel;
import com.ssafy.sulmap.core.model.ReviewModel;
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
public class GetBarResponse {
    private Long id;
    private String name;
    private String Address;
    private Double latitude;
    private Double longitude;
    private String baseCategoryName;
    private List<String> menus;
    private List<String> categories;
    private String open_information;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
