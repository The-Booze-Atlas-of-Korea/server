package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.request.CreatePlanRequest;
import com.ssafy.sulmap.api.dto.request.PlanSpotRequest;
import com.ssafy.sulmap.api.dto.request.UpdatePlanRequest;
import com.ssafy.sulmap.api.dto.response.PlanListResponse;
import com.ssafy.sulmap.api.dto.response.PlanResponse;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.DrinkingPlanSpotModel;
import com.ssafy.sulmap.core.model.command.CreatePlanCommand;
import com.ssafy.sulmap.core.model.command.UpdatePlanCommand;
import com.ssafy.sulmap.core.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService _planService;

    /**
     * 플랜 생성
     */
    @PostMapping
    public ResponseEntity<?> createPlan(
            @Valid @RequestBody CreatePlanRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var command = CreatePlanCommand.builder()
                .ownerUserId(userId)
                .title(request.title())
                .description(request.description())
                .theme(request.theme())
                .totalBudget(request.totalBudget())
                .spots(request.spots() == null
                        ? List.of()
                        : request.spots().stream()
                                .map(this::toSpotModel)
                                .toList())
                .build();

        var result = _planService.createPlan(command);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(PlanResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 플랜 목록 조회 (사용자별)
     */
    @GetMapping
    public ResponseEntity<?> listPlans(@AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var result = _planService.listPlans(userId);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        var plans = result.getOrThrow().stream()
                .map(PlanListResponse::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(plans);
    }

    /**
     * 플랜 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlan(
            @PathVariable("id") Long planId,
            @Valid @RequestBody UpdatePlanRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var command = UpdatePlanCommand.builder()
                .planId(planId)
                .userId(userId)
                .title(request.title())
                .description(request.description())
                .theme(request.theme())
                .totalBudget(request.totalBudget())
                .spots(request.spots() == null
                        ? null
                        : request.spots().stream()
                                .map(this::toSpotModel)
                                .toList())
                .build();

        var result = _planService.updatePlan(command);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(PlanResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 플랜 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlan(@PathVariable("id") Long planId) {
        var result = _planService.getPlan(planId);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(PlanResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 플랜 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(
            @PathVariable("id") Long planId,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        // deletePlan은 아직 구현되지 않았으므로 임시로 비워둠
        // CORE 레이어에 deletePlan 메서드가 필요함 (Follow-up)
        return ResponseEntity.ok().build();
    }

    /**
     * Request DTO -> Core Model 변환 헬퍼
     */
    private DrinkingPlanSpotModel toSpotModel(PlanSpotRequest request) {
        return DrinkingPlanSpotModel.builder()
                .placeId(request.placeId())
                .placeNameSnapshot(request.placeNameSnapshot())
                .placeAddressSnapshot(request.placeAddressSnapshot())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .sequence(request.sequence())
                .memo(request.memo())
                .build();
    }
}
