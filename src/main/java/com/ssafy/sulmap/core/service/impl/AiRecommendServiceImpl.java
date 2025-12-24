package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.MinorRecommendedBarModel;
import com.ssafy.sulmap.core.model.RecommendedBarModel;
import com.ssafy.sulmap.core.model.query.GetRecommenedBarsQuery;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.core.repository.AiRecommendRepository;
import com.ssafy.sulmap.core.service.AIRecommendService;
import com.ssafy.sulmap.core.service.BarService;
import com.ssafy.sulmap.core.service.UserService;
import com.ssafy.sulmap.share.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
@RequiredArgsConstructor
public class AiRecommendServiceImpl implements AIRecommendService {
    private final AiRecommendRepository _aiRecommendRepository;
    private final BarService _barService;
    private final UserService _userService;

    @Override
    public Result<List<RecommendedBarModel>> getRecommendedBars(GetRecommenedBarsQuery query) {
        final int FETCH_CANDIDATES = 200;
        final int STAGE2_MAX = 40;
        final int BATCH_SIZE = 100;
        final int STAGE1_PICK = 20;
        final int FINAL_TOPK = 10;

        var findBarsResult = _barService.findNearbyBars(
                new NearbyBarsQuery(query.getLat(), query.getLon(), query.getMaxDistance(),
                        FETCH_CANDIDATES, null, null, "distance")
        );
        if (findBarsResult.isFailure()) return Result.fail(findBarsResult.getErrors());

        var findUserResult = _userService.findUserById(query.getUserId());
        if (findUserResult.isFailure()) return Result.fail(findUserResult.getErrors());

        var bars = findBarsResult.getOrThrow();
        var userModel = findUserResult.getOrThrow();

        if (bars.isEmpty()) {
            return Result.ok(List.of());
        }

        // 2차에 들어갈 후보 풀
        List<BarListItemModel> stage2Candidates;

        if (bars.size() <= STAGE2_MAX) {
            stage2Candidates = bars;
        } else {
            stage2Candidates = new ArrayList<>(STAGE2_MAX);

            for (int start = 0; start < bars.size() && stage2Candidates.size() < STAGE2_MAX; start += BATCH_SIZE) {
                int end = Math.min(start + BATCH_SIZE, bars.size());
                var batch = new ArrayList<>(bars.subList(start, end)); // subList view 방지
                // 1차: 배치당 Top 5 (강제)
                var picked = _aiRecommendRepository.getMinorRecommend(batch, userModel, query, STAGE1_PICK);
                // 혹시라도 5개 초과로 오면 자름(안전장치)
                for (int i = 0; i < picked.size() && i < STAGE1_PICK && stage2Candidates.size() < STAGE2_MAX; i++) {
                    stage2Candidates.add(picked.get(i));
                }
            }
        }

        // 2차: 최종 Top 10 + 이유 생성 (강제)
        var finalResult = _aiRecommendRepository.getRecommend(stage2Candidates, userModel, query, FINAL_TOPK);

        return Result.ok(finalResult);
    }
}
