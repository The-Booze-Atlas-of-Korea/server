package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.request.GetRecommendedBarsRequest;
import com.ssafy.sulmap.api.dto.response.RecommendedBarItemResponse;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.query.GetRecommenedBarsQuery;
import com.ssafy.sulmap.core.service.AIRecommendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIRecommendController {

    private final AIRecommendService aiRecommendService;

    /**
     * POST /api/ai/recommend-bars
     */
    @PostMapping("/recommend-bars")
    public ResponseEntity<?> recommendBars(@Valid @RequestBody GetRecommendedBarsRequest req,
                                           @AuthenticationPrincipal UserDetail userDetail) {
        GetRecommenedBarsQuery query = new GetRecommenedBarsQuery();
        query.setLat(req.lat());
        query.setLon(req.lon());
        query.setUserId(userDetail.userModel().getId());
        query.setMaxDistance(req.maxDistance());
        query.setUserPrompt(req.userPrompt());
        var result = aiRecommendService.getRecommendedBars(query);
        if (result.isFailure()) {
            // 너희 Result 에러 구조에 맞춰서 그대로 내려주거나, 표준 에러 응답으로 변환하면 됨.
            // 여기선 단순히 errors를 내려줌.
            return ResponseEntity.badRequest().body(result.getErrors());
        }
        var items = result.getOrThrow().stream()
                .map(RecommendedBarItemResponse::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
}
