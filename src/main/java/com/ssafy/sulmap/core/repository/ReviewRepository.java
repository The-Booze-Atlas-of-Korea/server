package com.ssafy.sulmap.core.repository;

import com.ssafy.sulmap.core.model.ReviewModel;
import com.ssafy.sulmap.core.model.ReviewReportModel;
import com.ssafy.sulmap.core.model.ReviewSummaryModel;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 저장소 인터페이스
 */
public interface ReviewRepository {
    /**
     * 리뷰 저장 (생성 또는 수정)
     */
    ReviewModel save(ReviewModel review);

    /**
     * ID로 리뷰 조회
     */
    Optional<ReviewModel> findById(Long id);

    /**
     * 술집별 리뷰 목록 조회
     */
    List<ReviewModel> findByBarId(Long barId, String sort, int offset, int limit);

    /**
     * 사용자별 리뷰 목록 조회
     */
    List<ReviewModel> findByUserId(Long userId, int offset, int limit);

    /**
     * 리뷰 삭제 (soft delete)
     */
    void delete(Long id);

    /**
     * 술집의 리뷰 요약 통계 조회
     */
    ReviewSummaryModel getSummary(Long barId);

    /**
     * 리뷰 신고 저장
     */
    void saveReport(ReviewReportModel report);
}
