package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.RecommendedBarModel;
import com.ssafy.sulmap.share.result.Result;

import java.util.List;

public interface AIRecommendService {
    Result<List<RecommendedBarModel>> getRecommendedBars();
}
