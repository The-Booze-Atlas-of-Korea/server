package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.BarCategoryModel;
import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.core.model.MemoModel;
import com.ssafy.sulmap.core.model.ReviewModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

public record GetBarResponse(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String baseCategoryName,
        List<String> menus,
        List<String> categories,
        String openInformation,
        Date createdAt,
        Date updatedAt,
        Date deletedAt
) {
    public static GetBarResponse fromModel(BarModel m) {
        List<String> categoryNames = (m.getCategories() == null)
                ? List.of()
                : m.getCategories().stream().map(BarCategoryModel::getName).toList();

        return new GetBarResponse(
                m.getId(),
                m.getName(),
                m.getAddress(),
                m.getLatitude(),
                m.getLongitude(),
                m.getBaseCategoryName(),
                m.getMenus(),
                categoryNames,
                // BarModel에 open_information 필드가 섞여 있어서 JSON은 openInformation으로 통일 권장 :contentReference[oaicite:8]{index=8}
                m.getOpen_information(),
                m.getCreatedAt(),
                m.getUpdatedAt(),
                m.getDeletedAt()
        );
    }
}