package com.ssafy.sulmap.infra.mapper;

import com.ssafy.sulmap.infra.model.ReviewEntity;
import com.ssafy.sulmap.infra.model.ReviewMediaEntity;
import com.ssafy.sulmap.infra.model.ReviewReportEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 리뷰 MyBatis Mapper
 */
@Mapper
public interface ReviewMapper {
    // ***** 리뷰 기본 CRUD *****
    int insertReview(ReviewEntity entity);

    int updateReview(ReviewEntity entity);

    ReviewEntity selectById(@Param("id") Long id);

    List<ReviewEntity> selectByBarId(
            @Param("barId") Long barId,
            @Param("sort") String sort,
            @Param("offset") int offset,
            @Param("limit") int limit);

    List<ReviewEntity> selectByUserId(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    int softDeleteReview(@Param("id") Long id);

    // ***** 리뷰 미디어 *****
    int insertReviewMedia(ReviewMediaEntity entity);

    List<ReviewMediaEntity> selectMediaByReviewId(@Param("reviewId") Long reviewId);

    int deleteMediaByReviewId(@Param("reviewId") Long reviewId);

    // ***** 리뷰 요약 통계 *****
    Long countByBarId(@Param("barId") Long barId);

    Double averageRatingByBarId(@Param("barId") Long barId);

    List<Map<String, Object>> ratingDistributionByBarId(@Param("barId") Long barId);

    // ***** 리뷰 신고 *****
    int insertReviewReport(ReviewReportEntity entity);
}
