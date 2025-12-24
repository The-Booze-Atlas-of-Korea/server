package com.ssafy.sulmap.core.service.impl;

import com.ssafy.sulmap.core.model.ReviewMediaModel;
import com.ssafy.sulmap.core.model.ReviewModel;
import com.ssafy.sulmap.core.model.ReviewSummaryModel;
import com.ssafy.sulmap.core.model.command.CreateReviewCommand;
import com.ssafy.sulmap.core.model.command.ReportReviewCommand;
import com.ssafy.sulmap.core.model.command.UpdateReviewCommand;
import com.ssafy.sulmap.core.model.enums.MediaType;
import com.ssafy.sulmap.core.repository.ReviewRepository;
import com.ssafy.sulmap.share.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewServiceImpl 단위 테스트")
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private ReviewModel mockReview;
    private CreateReviewCommand validCreateCommand;
    private UpdateReviewCommand validUpdateCommand;

    @BeforeEach
    void setUp() {
        List<ReviewMediaModel> mediaList = List.of(
                ReviewMediaModel.builder()
                        .id(1L)
                        .reviewId(1L)
                        .mediaType(MediaType.IMAGE)
                        .url("https://example.com/image.jpg")
                        .orderIndex(0)
                        .build());

        mockReview = ReviewModel.builder()
                .id(1L)
                .userId(1L)
                .barId(1L)
                .visitId(null)
                .rating(5)
                .content("훌륭한 곳입니다!")
                .media(mediaList)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        validCreateCommand = CreateReviewCommand.builder()
                .userId(1L)
                .barId(1L)
                .visitId(null)
                .rating(5)
                .content("훌륭한 곳입니다!")
                .mediaUrls(List.of("https://example.com/image.jpg"))
                .build();

        validUpdateCommand = UpdateReviewCommand.builder()
                .reviewId(1L)
                .userId(1L)
                .rating(4)
                .content("수정된 리뷰")
                .mediaUrls(List.of())
                .build();
    }

    @Test
    @DisplayName("리뷰 요약 통계 조회 성공")
    void getSummary_Success() {
        // given
        Map<Integer, Long> distribution = new HashMap<>();
        distribution.put(5, 10L);
        distribution.put(4, 5L);

        ReviewSummaryModel summary = ReviewSummaryModel.builder()
                .totalCount(15L)
                .averageRating(4.5)
                .ratingDistribution(distribution)
                .build();

        when(reviewRepository.getSummary(1L)).thenReturn(summary);

        // when
        Result<ReviewSummaryModel> result = reviewService.getSummary(1L);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOrThrow().getTotalCount()).isEqualTo(15L);
        assertThat(result.getOrThrow().getAverageRating()).isEqualTo(4.5);
        verify(reviewRepository, times(1)).getSummary(1L);
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void listReviews_Success() {
        // given
        List<ReviewModel> reviews = List.of(mockReview);
        when(reviewRepository.findByBarId(1L, "LATEST", 0, 10)).thenReturn(reviews);

        // when
        Result<List<ReviewModel>> result = reviewService.listReviews(1L, "LATEST", 0, 10);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOrThrow()).hasSize(1);
        verify(reviewRepository, times(1)).findByBarId(1L, "LATEST", 0, 10);
    }

    @Test
    @DisplayName("리뷰 상세 조회 성공")
    void getReviewDetail_Success() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mockReview));

        // when
        Result<ReviewModel> result = reviewService.getReviewDetail(1L);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOrThrow().getId()).isEqualTo(1L);
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰 조회 시 실패")
    void getReviewDetail_NotFound_Fail() {
        // given
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Result<ReviewModel> result = reviewService.getReviewDetail(999L);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_Success() {
        // given
        when(reviewRepository.save(any(ReviewModel.class))).thenReturn(mockReview);

        // when
        Result<ReviewModel> result = reviewService.createReview(validCreateCommand);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOrThrow()).isNotNull();
        verify(reviewRepository, times(1)).save(any(ReviewModel.class));
    }

    @Test
    @DisplayName("별점이 1보다 작으면 생성 실패")
    void createReview_RatingTooLow_Fail() {
        // given
        CreateReviewCommand invalidCommand = CreateReviewCommand.builder()
                .userId(1L)
                .barId(1L)
                .rating(0)
                .content("내용")
                .build();

        // when
        Result<ReviewModel> result = reviewService.createReview(invalidCommand);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("별점이 5보다 크면 생성 실패")
    void createReview_RatingTooHigh_Fail() {
        // given
        CreateReviewCommand invalidCommand = CreateReviewCommand.builder()
                .userId(1L)
                .barId(1L)
                .rating(6)
                .content("내용")
                .build();

        // when
        Result<ReviewModel> result = reviewService.createReview(invalidCommand);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("내용이 비어있으면 생성 실패")
    void createReview_EmptyContent_Fail() {
        // given
        CreateReviewCommand invalidCommand = CreateReviewCommand.builder()
                .userId(1L)
                .barId(1L)
                .rating(5)
                .content("")
                .build();

        // when
        Result<ReviewModel> result = reviewService.createReview(invalidCommand);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("리뷰 수정 성공 (작성자)")
    void updateReview_Success() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mockReview));
        when(reviewRepository.save(any(ReviewModel.class))).thenReturn(mockReview);

        // when
        Result<ReviewModel> result = reviewService.updateReview(validUpdateCommand);

        // then
        assertThat(result.isSuccess()).isTrue();
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(ReviewModel.class));
    }

    @Test
    @DisplayName("다른 사용자가 리뷰 수정 시 권한 오류")
    void updateReview_Forbidden() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mockReview));

        UpdateReviewCommand otherUserCommand = UpdateReviewCommand.builder()
                .reviewId(1L)
                .userId(999L) // 다른 사용자
                .rating(4)
                .content("수정")
                .build();

        // when
        Result<ReviewModel> result = reviewService.updateReview(otherUserCommand);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("리뷰 삭제 성공 (작성자)")
    void deleteReview_Success() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mockReview));

        // when
        Result<Void> result = reviewService.deleteReview(1L, 1L);

        // then
        assertThat(result.isSuccess()).isTrue();
        verify(reviewRepository, times(1)).delete(1L);
    }

    @Test
    @DisplayName("다른 사용자가 리뷰 삭제 시 권한 오류")
    void deleteReview_Forbidden() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mockReview));

        // when
        Result<Void> result = reviewService.deleteReview(1L, 999L);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    @DisplayName("내 리뷰 목록 조회 성공")
    void listMyReviews_Success() {
        // given
        List<ReviewModel> reviews = List.of(mockReview);
        when(reviewRepository.findByUserId(1L, 0, 10)).thenReturn(reviews);

        // when
        Result<List<ReviewModel>> result = reviewService.listMyReviews(1L, 0, 10);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOrThrow()).hasSize(1);
        verify(reviewRepository, times(1)).findByUserId(1L, 0, 10);
    }

    @Test
    @DisplayName("리뷰 신고 성공")
    void reportReview_Success() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mockReview));

        ReportReviewCommand command = ReportReviewCommand.builder()
                .reviewId(1L)
                .reporterId(2L)
                .reason("부적절한 내용")
                .build();

        // when
        Result<Void> result = reviewService.reportReview(command);

        // then
        assertThat(result.isSuccess()).isTrue();
        verify(reviewRepository, times(1)).saveReport(any());
    }

    @Test
    @DisplayName("신고 사유가 비어있으면 실패")
    void reportReview_EmptyReason_Fail() {
        // given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(mockReview));

        ReportReviewCommand command = ReportReviewCommand.builder()
                .reviewId(1L)
                .reporterId(2L)
                .reason("")
                .build();

        // when
        Result<Void> result = reviewService.reportReview(command);

        // then
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getSingleErrorOrThrow().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        verify(reviewRepository, never()).saveReport(any());
    }
}
