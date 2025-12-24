package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.ReviewMediaModel;
import com.ssafy.sulmap.core.model.ReviewModel;
import com.ssafy.sulmap.core.model.ReviewReportModel;
import com.ssafy.sulmap.core.model.ReviewSummaryModel;
import com.ssafy.sulmap.core.model.command.CreateReviewCommand;
import com.ssafy.sulmap.core.model.command.ReportReviewCommand;
import com.ssafy.sulmap.core.model.command.UpdateReviewCommand;
import com.ssafy.sulmap.core.model.enums.MediaType;
import com.ssafy.sulmap.core.model.enums.ReportStatus;
import com.ssafy.sulmap.core.repository.ReviewRepository;
import com.ssafy.sulmap.core.service.ReviewService;
import com.ssafy.sulmap.share.result.error.impl.SimpleError;
import com.ssafy.sulmap.share.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 리뷰 서비스 구현
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository _reviewRepository;

    @Override
    public Result<ReviewSummaryModel> getSummary(Long barId) {
        ReviewSummaryModel summary = _reviewRepository.getSummary(barId);
        return Result.ok(summary);
    }

    @Override
    public Result<List<ReviewModel>> listReviews(Long barId, String sort, int page, int size) {
        int offset = page * size;
        List<ReviewModel> reviews = _reviewRepository.findByBarId(barId, sort, offset, size);
        return Result.ok(reviews);
    }

    @Override
    public Result<ReviewModel> getReviewDetail(Long reviewId) {
        Optional<ReviewModel> review = _reviewRepository.findById(reviewId);

        if (review.isEmpty()) {
            return Result.fail(new SimpleError(HttpStatus.NOT_FOUND.value(), "리뷰를 찾을 수 없습니다."));
        }

        return Result.ok(review.get());
    }

    @Override
    public Result<ReviewModel> createReview(CreateReviewCommand command) {
        // Validation
        if (command.rating() < 1 || command.rating() > 5) {
            return Result.fail(new SimpleError(HttpStatus.BAD_REQUEST.value(), "별점은 1-5 사이의 값이어야 합니다."));
        }

        if (command.content() == null || command.content().isBlank()) {
            return Result.fail(new SimpleError(HttpStatus.BAD_REQUEST.value(), "리뷰 내용은 필수입니다."));
        }

        // 미디어 모델 변환
        List<ReviewMediaModel> mediaModels = new ArrayList<>();
        if (command.mediaUrls() != null && !command.mediaUrls().isEmpty()) {
            for (int i = 0; i < command.mediaUrls().size(); i++) {
                String url = command.mediaUrls().get(i);
                MediaType mediaType = MediaType.IMAGE; // 기본값, 추후 확장 가능

                ReviewMediaModel mediaModel = ReviewMediaModel.builder()
                        .url(url)
                        .mediaType(mediaType)
                        .orderIndex(i)
                        .build();
                mediaModels.add(mediaModel);
            }
        }

        // 리뷰 모델 생성
        ReviewModel review = ReviewModel.builder()
                .userId(command.userId())
                .barId(command.barId())
                .visitId(command.visitId())
                .rating(command.rating())
                .content(command.content())
                .media(mediaModels)
                .build();

        ReviewModel saved = _reviewRepository.save(review);
        return Result.ok(saved);
    }

    @Override
    public Result<ReviewModel> updateReview(UpdateReviewCommand command) {
        // 기존 리뷰 조회
        Optional<ReviewModel> existingOpt = _reviewRepository.findById(command.reviewId());
        if (existingOpt.isEmpty()) {
            return Result.fail(new SimpleError(HttpStatus.NOT_FOUND.value(), "리뷰를 찾을 수 없습니다."));
        }

        ReviewModel existing = existingOpt.get();

        // 권한 검증 (작성자만 수정 가능)
        if (!existing.getUserId().equals(command.userId())) {
            return Result.fail(new SimpleError(HttpStatus.FORBIDDEN.value(), "리뷰 작성자만 수정할 수 있습니다."));
        }

        // Validation
        if (command.rating() != null && (command.rating() < 1 || command.rating() > 5)) {
            return Result.fail(new SimpleError(HttpStatus.BAD_REQUEST.value(), "별점은 1-5 사이의 값이어야 합니다."));
        }

        // 업데이트
        if (command.rating() != null) {
            existing.setRating(command.rating());
        }
        if (command.content() != null) {
            existing.setContent(command.content());
        }

        // 미디어 업데이트
        if (command.mediaUrls() != null) {
            List<ReviewMediaModel> mediaModels = new ArrayList<>();
            for (int i = 0; i < command.mediaUrls().size(); i++) {
                String url = command.mediaUrls().get(i);
                MediaType mediaType = MediaType.IMAGE;

                ReviewMediaModel mediaModel = ReviewMediaModel.builder()
                        .url(url)
                        .mediaType(mediaType)
                        .orderIndex(i)
                        .build();
                mediaModels.add(mediaModel);
            }
            existing.setMedia(mediaModels);
        }

        ReviewModel updated = _reviewRepository.save(existing);
        return Result.ok(updated);
    }

    @Override
    public Result<Void> deleteReview(Long reviewId, Long userId) {
        // 기존 리뷰 조회
        Optional<ReviewModel> existingOpt = _reviewRepository.findById(reviewId);
        if (existingOpt.isEmpty()) {
            return Result.fail(new SimpleError(HttpStatus.NOT_FOUND.value(), "리뷰를 찾을 수 없습니다."));
        }

        ReviewModel existing = existingOpt.get();

        // 권한 검증 (작성자만 삭제 가능)
        if (!existing.getUserId().equals(userId)) {
            return Result.fail(new SimpleError(HttpStatus.FORBIDDEN.value(), "리뷰 작성자만 삭제할 수 있습니다."));
        }

        _reviewRepository.delete(reviewId);
        return Result.ok(null);
    }

    @Override
    public Result<List<ReviewModel>> listMyReviews(Long userId, int page, int size) {
        int offset = page * size;
        List<ReviewModel> reviews = _reviewRepository.findByUserId(userId, offset, size);
        return Result.ok(reviews);
    }

    @Override
    public Result<Void> reportReview(ReportReviewCommand command) {
        // 리뷰 존재 여부 확인
        Optional<ReviewModel> review = _reviewRepository.findById(command.reviewId());
        if (review.isEmpty()) {
            return Result.fail(new SimpleError(HttpStatus.NOT_FOUND.value(), "리뷰를 찾을 수 없습니다."));
        }

        // Validation
        if (command.reason() == null || command.reason().isBlank()) {
            return Result.fail(new SimpleError(HttpStatus.BAD_REQUEST.value(), "신고 사유는 필수입니다."));
        }

        // 신고 모델 생성
        ReviewReportModel report = ReviewReportModel.builder()
                .reviewId(command.reviewId())
                .reporterId(command.reporterId())
                .reason(command.reason())
                .status(ReportStatus.PENDING)
                .build();

        _reviewRepository.saveReport(report);
        return Result.ok(null);
    }
}
