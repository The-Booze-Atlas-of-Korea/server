package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.MinorRecommendedBarModel;
import com.ssafy.sulmap.core.model.RecommendedBarModel;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.model.query.GetRecommenedBarsQuery;

import java.util.List;

public interface AiRecommendRepository {
    //1차 추천
    List<MinorRecommendedBarModel> getMinorRecommend(List<BarListItemModel> models, UserModel user, GetRecommenedBarsQuery query, int topK);

    //2차 추천
    List<RecommendedBarModel> getRecommend(List<BarListItemModel> models, UserModel user, GetRecommenedBarsQuery query, int topK);
}
