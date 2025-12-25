package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.request.UpsertMemoRequest;
import com.ssafy.sulmap.api.dto.response.MemoResponse;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.command.UpsertMemoCommand;
import com.ssafy.sulmap.core.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 메모 컨트롤러
 */
@RestController
@RequestMapping("/bars/{barId}/memo")
@RequiredArgsConstructor
public class MemoController {
    private final MemoService _memoService;

    /**
     * 메모 생성/수정 (Upsert)
     */
    @PutMapping
    public ResponseEntity<?> upsertMemo(
            @PathVariable Long barId,
            @Valid @RequestBody UpsertMemoRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {

        var userId = userDetail.userModel().getId();

        var command = UpsertMemoCommand.builder()
                .userId(userId)
                .barId(barId)
                .content(request.content())
                .build();

        var result = _memoService.upsertMemo(command);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(MemoResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 메모 조회
     */
    @GetMapping
    public ResponseEntity<?> getMemo(
            @PathVariable Long barId,
            @AuthenticationPrincipal UserDetail userDetail) {

        var userId = userDetail.userModel().getId();
        var result = _memoService.getMemo(userId, barId);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(MemoResponse.fromModel(result.getOrThrow()));
    }
}
