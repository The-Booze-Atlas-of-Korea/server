package com.ssafy.sulmap.core.service;

import com.ssafy.sulmap.core.model.ReviewModel;
import com.ssafy.sulmap.core.model.ReviewSummaryModel;
import com.ssafy.sulmap.core.model.command.CreateReviewCommand;
import com.ssafy.sulmap.core.model.command.ReportReviewCommand;
import com.ssafy.sulmap.core.model.command.UpdateReviewCommand;
import com.ssafy.sulmap.share.result.Result;

import java.util.List;

/**
 * 리뷰 서비스 인터페이스
 */
public interface ReviewService {
    /**
     * 술집의 리뷰 요약 통계 조회
     */
    Result<ReviewSummaryModel> getSummary(Long barId);

    /**
     * 술집의 리뷰 목록 조회
     */
    Result<List<ReviewModel>> listReviews(Long barId, String sort, int page, int size);

    /**
     * 리뷰 상세 조회
     */
    Result<ReviewModel> getReviewDetail(Long reviewId);

    /**
     * 리뷰 생성
     */
    Result<ReviewModel> createReview(CreateReviewCommand command);

    /**
     * 리뷰 수정
     */
    Result<ReviewModel> updateReview(UpdateReviewCommand command);

    /**
     * 리뷰 삭제 (soft delete)
     */
    Result<Void> deleteReview(Long reviewId, Long userId);

    /**
     * 내 리뷰 목록 조회
     */
    Result<List<ReviewModel>> listMyReviews(Long userId, int page, int size);

    /**
     * 리뷰 신고
     */
    Result<Void> reportReview(ReportReviewCommand command);
}
