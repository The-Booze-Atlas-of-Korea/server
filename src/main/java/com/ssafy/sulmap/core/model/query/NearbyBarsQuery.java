package com.ssafy.sulmap.core.model.query;

public record NearbyBarsQuery(
        double latitude,
        double longitude,
        int radiusMeters,
        int count,
        String keyword,        // optional: 상호/카테고리/태그 검색
        String category,       // optional: 주점/이자카야 등
        String sort            // optional: distance|recommended|rating ...
) {
}
