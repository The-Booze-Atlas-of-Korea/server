package com.ssafy.sulmap.infra.repository;

import com.ssafy.sulmap.core.model.ReviewMediaModel;
import com.ssafy.sulmap.core.model.ReviewModel;
import com.ssafy.sulmap.core.model.ReviewReportModel;
import com.ssafy.sulmap.core.model.ReviewSummaryModel;
import com.ssafy.sulmap.core.repository.ReviewRepository;
import com.ssafy.sulmap.infra.mapper.ReviewMapper;
import com.ssafy.sulmap.infra.model.ReviewEntity;
import com.ssafy.sulmap.infra.model.ReviewMediaEntity;
import com.ssafy.sulmap.infra.model.ReviewReportEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 리뷰 저장소 구현
 */
@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final ReviewMapper _reviewMapper;

    @Override
    @Transactional
    public ReviewModel save(ReviewModel review) {
        if (review.getId() == null) {
            // 신규 리뷰 생성
            return insertNewReview(review);
        } else {
            // 기존 리뷰 수정
            return updateExistingReview(review);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewModel> findById(Long id) {
        ReviewEntity reviewEntity = _reviewMapper.selectById(id);
        if (reviewEntity == null) {
            return Optional.empty();
        }

        List<ReviewMediaEntity> mediaEntities = _reviewMapper.selectMediaByReviewId(id);
        return Optional.of(reviewEntity.toModel(mediaEntities));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewModel> findByBarId(Long barId, String sort, int offset, int limit) {
        List<ReviewEntity> entities = _reviewMapper.selectByBarId(barId, sort, offset, limit);

        return entities.stream().map(entity -> {
            List<ReviewMediaEntity> mediaEntities = _reviewMapper.selectMediaByReviewId(entity.getId());
            return entity.toModel(mediaEntities);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewModel> findByUserId(Long userId, int offset, int limit) {
        List<ReviewEntity> entities = _reviewMapper.selectByUserId(userId, offset, limit);

        return entities.stream().map(entity -> {
            List<ReviewMediaEntity> mediaEntities = _reviewMapper.selectMediaByReviewId(entity.getId());
            return entity.toModel(mediaEntities);
        }).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        _reviewMapper.softDeleteReview(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryModel getSummary(Long barId) {
        Long totalCount = _reviewMapper.countByBarId(barId);
        Double averageRating = _reviewMapper.averageRatingByBarId(barId);
        List<Map<String, Object>> distributionList = _reviewMapper.ratingDistributionByBarId(barId);

        // 별점 분포를 Map으로 변환
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (Map<String, Object> row : distributionList) {
            Integer rating = ((Number) row.get("rating")).intValue();
            Long count = ((Number) row.get("count")).longValue();
            ratingDistribution.put(rating, count);
        }

        return ReviewSummaryModel.builder()
                .totalCount(totalCount)
                .averageRating(averageRating)
                .ratingDistribution(ratingDistribution)
                .build();
    }

    @Override
    @Transactional
    public void saveReport(ReviewReportModel report) {
        ReviewReportEntity entity = ReviewReportEntity.fromModel(report);
        _reviewMapper.insertReviewReport(entity);
    }

    /**
     * 신규 리뷰 삽입
     */
    private ReviewModel insertNewReview(ReviewModel review) {
        // 타임스탬프 설정
        LocalDateTime now = LocalDateTime.now();
        review.setCreatedAt(now);
        review.setUpdatedAt(now);

        // 리뷰 엔티티 변환 및 삽입
        ReviewEntity reviewEntity = ReviewEntity.fromModel(review);
        _reviewMapper.insertReview(reviewEntity);

        // 자동 생성된 ID를 리뷰 모델에 설정
        review.setId(reviewEntity.getId());

        // 미디어 삽입
        if (review.getMedia() != null && !review.getMedia().isEmpty()) {
            insertReviewMedia(review.getId(), review.getMedia());
        }

        return review;
    }

    /**
     * 기존 리뷰 수정 (미디어 전체 재구성)
     */
    private ReviewModel updateExistingReview(ReviewModel review) {
        // 타임스탬프 갱신
        review.setUpdatedAt(LocalDateTime.now());

        // 리뷰 정보 업데이트
        ReviewEntity reviewEntity = ReviewEntity.fromModel(review);
        _reviewMapper.updateReview(reviewEntity);

        // 기존 미디어 전체 삭제
        _reviewMapper.deleteMediaByReviewId(review.getId());

        // 새 미디어 삽입
        if (review.getMedia() != null && !review.getMedia().isEmpty()) {
            insertReviewMedia(review.getId(), review.getMedia());
        }

        return review;
    }

    /**
     * 리뷰 미디어 목록 삽입
     */
    private void insertReviewMedia(Long reviewId, List<ReviewMediaModel> media) {
        for (ReviewMediaModel mediaModel : media) {
            ReviewMediaEntity mediaEntity = ReviewMediaEntity.fromModel(mediaModel, reviewId);
            _reviewMapper.insertReviewMedia(mediaEntity);
            // 자동 생성된 ID를 미디어 모델에 설정
            mediaModel.setId(mediaEntity.getId());
            mediaModel.setReviewId(reviewId);
        }
    }
}
