package com.ssafy.sulmap.api.controller;

import com.ssafy.sulmap.api.dto.request.CreateReviewRequest;
import com.ssafy.sulmap.api.dto.request.ReportReviewRequest;
import com.ssafy.sulmap.api.dto.request.UpdateReviewRequest;
import com.ssafy.sulmap.api.dto.response.ReviewDetailResponse;
import com.ssafy.sulmap.api.dto.response.ReviewListItemResponse;
import com.ssafy.sulmap.api.dto.response.ReviewSummaryResponse;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.command.CreateReviewCommand;
import com.ssafy.sulmap.core.model.command.ReportReviewCommand;
import com.ssafy.sulmap.core.model.command.UpdateReviewCommand;
import com.ssafy.sulmap.core.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 컨트롤러
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService _reviewService;

    /**
     * 술집의 리뷰 요약 통계 조회
     */
    @GetMapping("/bars/{barId}/reviews/summary")
    public ResponseEntity<?> getReviewSummary(@PathVariable Long barId) {
        var result = _reviewService.getSummary(barId);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(ReviewSummaryResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 술집의 리뷰 목록 조회
     */
    @GetMapping("/bars/{barId}/reviews")
    public ResponseEntity<?> listReviews(
            @PathVariable Long barId,
            @RequestParam(defaultValue = "LATEST") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var result = _reviewService.listReviews(barId, sort, page, size);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        var reviews = result.getOrThrow().stream()
                .map(ReviewListItemResponse::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 상세 조회
     */
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<?> getReviewDetail(@PathVariable Long reviewId) {
        var result = _reviewService.getReviewDetail(reviewId);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(ReviewDetailResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 리뷰 생성
     */
    @PostMapping("/bars/{barId}/reviews")
    public ResponseEntity<?> createReview(
            @PathVariable Long barId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {

        var userId = userDetail.userModel().getId();

        var command = CreateReviewCommand.builder()
                .userId(userId)
                .barId(barId)
                .visitId(null) // MVP에서는 visit 연결 생략
                .rating(request.rating())
                .content(request.content())
                .mediaUrls(request.mediaUrls())
                .build();

        var result = _reviewService.createReview(command);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(ReviewDetailResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 리뷰 수정
     */
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {

        var userId = userDetail.userModel().getId();

        var command = UpdateReviewCommand.builder()
                .reviewId(reviewId)
                .userId(userId)
                .rating(request.rating())
                .content(request.content())
                .mediaUrls(request.mediaUrls())
                .build();

        var result = _reviewService.updateReview(command);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.ok(ReviewDetailResponse.fromModel(result.getOrThrow()));
    }

    /**
     * 리뷰 삭제
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetail userDetail) {

        var userId = userDetail.userModel().getId();
        var result = _reviewService.deleteReview(reviewId, userId);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * 내 리뷰 목록 조회
     */
    @GetMapping("/users/me/reviews")
    public ResponseEntity<?> listMyReviews(
            @AuthenticationPrincipal UserDetail userDetail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var userId = userDetail.userModel().getId();
        var result = _reviewService.listMyReviews(userId, page, size);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        var reviews = result.getOrThrow().stream()
                .map(ReviewListItemResponse::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviews);
    }

    /**
     * 리뷰 신고
     */
    @PostMapping("/reviews/{reviewId}/report")
    public ResponseEntity<?> reportReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReportReviewRequest request,
            @AuthenticationPrincipal UserDetail userDetail) {

        var userId = userDetail.userModel().getId();

        var command = ReportReviewCommand.builder()
                .reviewId(reviewId)
                .reporterId(userId)
                .reason(request.reason())
                .build();

        var result = _reviewService.reportReview(command);

        if (result.isFailure()) {
            return new ResponseEntity<>(result.getSingleErrorOrThrow().getStatus());
        }

        return ResponseEntity.status(201).build();
    }
}
