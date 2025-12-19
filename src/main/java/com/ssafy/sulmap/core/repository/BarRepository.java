package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;

import java.util.*;

public interface BarRepository {
    Optional<BarModel> findById(long barId);
    /**
     * FR2(+FR1): 현재 위치 기준 주변 술집 검색 (반경/필터/정렬/키워드 포함 가능)
     */
    List<BarListItemModel> findNearby(NearbyBarsQuery query);
}
