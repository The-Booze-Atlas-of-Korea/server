package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.BarModel;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.share.result.Result;

import java.util.List;

public interface BarService {
    /**
     * FR2(+FR1): 현재 위치 기준으로 주변 술집 검색 (반경/필터/정렬 등)
     */
    Result<List<BarModel>> findNearbyBars(NearbyBarsQuery query);
}
