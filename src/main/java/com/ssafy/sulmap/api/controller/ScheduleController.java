package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.request.CreateScheduleRequest;
import com.ssafy.sulmap.api.dto.request.UpdateScheduleRequest;
import com.ssafy.sulmap.api.dto.response.ScheduleResponse;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.command.CreateScheduleCommand;
import com.ssafy.sulmap.core.model.command.UpdateScheduleCommand;
import com.ssafy.sulmap.core.model.enums.ScheduleStatus;
import com.ssafy.sulmap.core.model.query.GetSchedulesInPeriodQuery;
import com.ssafy.sulmap.core.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService _scheduleService;

    /**
     * 일정 생성
     */
    @PostMapping
    public ResponseEntity<?> createSchedule(
            @Valid @RequestBody CreateScheduleRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var command = CreateScheduleCommand.builder()
                .ownerUserId(userId)
                .planId(request.planId())
                .scheduleTitle(request.scheduleTitle())
                .meetAt(request.meetAt())
                .build();

        var result = _scheduleService.createSchedule(command);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(ScheduleResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 일정 수정
     */
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable("id") Long scheduleId,
            @Valid @RequestBody UpdateScheduleRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var command = UpdateScheduleCommand.builder()
                .scheduleId(scheduleId)
                .userId(userId)
                .scheduleTitle(request.scheduleTitle())
                .meetAt(request.meetAt())
                .status(request.status() != null ? ScheduleStatus.fromString(request.status()) : null)
                .build();

        var result = _scheduleService.updateSchedule(command);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(ScheduleResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 일정 단건 조회
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getSchedule(@PathVariable("id") Long scheduleId) {
        var result = _scheduleService.getSchedule(scheduleId);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(ScheduleResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 일정 히스토리 조회 (페이징)
     */
    @GetMapping("/history")
    public ResponseEntity<?> getScheduleHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var result = _scheduleService.getScheduleHistory(userId, page, size);
        if (result.isFailure()) {
            return ResponseEntity.status(result.getSingleErrorOrThrow().getStatus()).build();
        }

        var schedules = result.getOrThrow().stream()
                .map(ScheduleResponse::fromModel)
                .toList();

        return ResponseEntity.ok(schedules);
    }

    /**
     * 기간별 일정 조회 (캘린더용)
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> getSchedulesByPeriod(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var query = new GetSchedulesInPeriodQuery(userId, from, to);
        var result = _scheduleService.getSchedulesInPeriod(query);
        if (result.isFailure()) {
            return ResponseEntity.status(result.getSingleErrorOrThrow().getStatus()).build();
        }

        var schedules = result.getOrThrow().stream()
                .map(ScheduleResponse::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(schedules);
    }

    /**
     * 일정 삭제
     */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable("id") Long scheduleId,
            @AuthenticationPrincipal UserDetail userDetail) {
        var userId = userDetail.userModel().getId();

        var result = _scheduleService.deleteSchedule(scheduleId, userId);
        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok().build();
    }
}
