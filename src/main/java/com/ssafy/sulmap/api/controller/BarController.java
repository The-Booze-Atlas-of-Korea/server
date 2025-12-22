package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.request.FindNearByBarRequest;
import com.ssafy.sulmap.api.dto.response.GetBarResponse;
import com.ssafy.sulmap.core.model.BarListItemModel;
import com.ssafy.sulmap.core.model.query.NearbyBarsQuery;
import com.ssafy.sulmap.core.service.BarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BarController {

    private final BarService barService;

    /**
     * 문서: POST /api/bars/nearby :contentReference[oaicite:12]{index=12}
     */
    @PostMapping("/bars/nearby")
    public List<BarListItemModel> findNearby(@Valid @RequestBody FindNearByBarRequest req) {
        NearbyBarsQuery query = new NearbyBarsQuery(req.latitude(), req.longitude(), req.radiusMeters(), req.count(), req.keyword(), req.category(), req.sort());

        return barService.findNearbyBars(query).getOrThrow();
    }

    /**
     * 문서: GET /bars/{barId} :contentReference[oaicite:14]{index=14}
     */
    @GetMapping("/bars/{barId}")
    public GetBarResponse getBar(@PathVariable @Positive long barId) {
        return GetBarResponse.fromModel(barService.findBarById(barId).getOrThrow());
    }
}
