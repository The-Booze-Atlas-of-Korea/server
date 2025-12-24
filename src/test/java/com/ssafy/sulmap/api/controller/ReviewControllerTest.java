package com.ssafy.sulmap.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.sulmap.api.dto.request.CreateReviewRequest;
import com.ssafy.sulmap.api.dto.request.ReportReviewRequest;
import com.ssafy.sulmap.api.dto.request.UpdateReviewRequest;
import com.ssafy.sulmap.api.security.model.UserDetail;
import com.ssafy.sulmap.core.model.ReviewModel;
import com.ssafy.sulmap.core.model.ReviewSummaryModel;
import com.ssafy.sulmap.core.model.UserModel;
import com.ssafy.sulmap.core.service.ReviewService;
import com.ssafy.sulmap.share.result.Result;
import com.ssafy.sulmap.share.result.error.impl.SimpleError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ReviewController 통합 테스트")
class ReviewControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ReviewService reviewService;

        private UserDetail userDetail;
        private ReviewModel mockReview;
        private ReviewSummaryModel mockSummary;

        @BeforeEach
        void setUp() {
                UserModel userModel = UserModel.builder()
                                .id(1L)
                                .loginId("testuser")
                                .name("테스트유저")
                                .build();

                userDetail = new UserDetail(userModel);

                mockReview = ReviewModel.builder()
                                .id(1L)
                                .userId(1L)
                                .barId(1L)
                                .rating(5)
                                .content("훌륭한 곳입니다!")
                                .media(List.of())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                Map<Integer, Long> distribution = new HashMap<>();
                distribution.put(5, 10L);
                distribution.put(4, 5L);

                mockSummary = ReviewSummaryModel.builder()
                                .totalCount(15L)
                                .averageRating(4.5)
                                .ratingDistribution(distribution)
                                .build();
        }

        @Test
        @DisplayName("리뷰 요약 통계 조회 성공")
        void getReviewSummary_Success() throws Exception {
                // given
                when(reviewService.getSummary(eq(1L))).thenReturn(Result.ok(mockSummary));

                // when & then
                mockMvc.perform(get("/api/bars/1/reviews/summary"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalCount").value(15))
                                .andExpect(jsonPath("$.averageRating").value(4.5));
        }

        @Test
        @DisplayName("리뷰 목록 조회 성공")
        void listReviews_Success() throws Exception {
                // given
                when(reviewService.listReviews(eq(1L), eq("LATEST"), eq(0), eq(10)))
                                .thenReturn(Result.ok(List.of(mockReview)));

                // when & then
                mockMvc.perform(get("/api/bars/1/reviews")
                                .param("sort", "LATEST")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].rating").value(5));
        }

        @Test
        @DisplayName("리뷰 상세 조회 성공")
        void getReviewDetail_Success() throws Exception {
                // given
                when(reviewService.getReviewDetail(eq(1L))).thenReturn(Result.ok(mockReview));

                // when & then
                mockMvc.perform(get("/api/reviews/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.content").value("훌륭한 곳입니다!"));
        }

        @Test
        @DisplayName("존재하지 않는 리뷰 조회 시 404")
        void getReviewDetail_NotFound() throws Exception {
                // given
                when(reviewService.getReviewDetail(eq(999L)))
                                .thenReturn(Result
                                                .fail(new SimpleError(HttpStatus.NOT_FOUND.value(), "리뷰를 찾을 수 없습니다.")));

                // when & then
                mockMvc.perform(get("/api/reviews/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("리뷰 생성 성공")
        void createReview_Success() throws Exception {
                // given
                CreateReviewRequest request = new CreateReviewRequest(
                                5,
                                "훌륭한 곳입니다!",
                                List.of("https://example.com/image.jpg"));
                when(reviewService.createReview(any())).thenReturn(Result.ok(mockReview));

                // when & then
                mockMvc.perform(post("/api/bars/1/reviews")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.rating").value(5));
        }

        @Test
        @WithMockUser
        @DisplayName("별점이 범위를 벗어나면 400")
        void createReview_InvalidRating_BadRequest() throws Exception {
                // given
                CreateReviewRequest request = new CreateReviewRequest(
                                6, // 잘못된 별점
                                "내용",
                                List.of());

                // when & then
                mockMvc.perform(post("/api/bars/1/reviews")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("리뷰 수정 성공")
        void updateReview_Success() throws Exception {
                // given
                UpdateReviewRequest request = new UpdateReviewRequest(
                                4,
                                "수정된 리뷰",
                                List.of());
                when(reviewService.updateReview(any())).thenReturn(Result.ok(mockReview));

                // when & then
                mockMvc.perform(patch("/api/reviews/1")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        @DisplayName("권한 없이 리뷰 수정 시 403")
        void updateReview_Forbidden() throws Exception {
                // given
                UpdateReviewRequest request = new UpdateReviewRequest(4, "수정", List.of());
                when(reviewService.updateReview(any()))
                                .thenReturn(Result.fail(new SimpleError(HttpStatus.FORBIDDEN.value(), "권한 없음")));

                // when & then
                mockMvc.perform(patch("/api/reviews/1")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("리뷰 삭제 성공")
        void deleteReview_Success() throws Exception {
                // given
                when(reviewService.deleteReview(eq(1L), eq(1L))).thenReturn(Result.ok(null));

                // when & then
                mockMvc.perform(delete("/api/reviews/1")
                                .with(user(userDetail)))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser
        @DisplayName("권한 없이 리뷰 삭제 시 403")
        void deleteReview_Forbidden() throws Exception {
                // given
                when(reviewService.deleteReview(eq(1L), eq(1L)))
                                .thenReturn(Result.fail(new SimpleError(HttpStatus.FORBIDDEN.value(), "권한 없음")));

                // when & then
                mockMvc.perform(delete("/api/reviews/1")
                                .with(user(userDetail)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("내 리뷰 목록 조회 성공")
        void listMyReviews_Success() throws Exception {
                // given
                when(reviewService.listMyReviews(eq(1L), eq(0), eq(10)))
                                .thenReturn(Result.ok(List.of(mockReview)));

                // when & then
                mockMvc.perform(get("/api/users/me/reviews")
                                .with(user(userDetail))
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @WithMockUser
        @DisplayName("리뷰 신고 성공")
        void reportReview_Success() throws Exception {
                // given
                ReportReviewRequest request = new ReportReviewRequest("부적절한 내용");
                when(reviewService.reportReview(any())).thenReturn(Result.ok(null));

                // when & then
                mockMvc.perform(post("/api/reviews/1/report")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());
        }

        @Test
        @WithMockUser
        @DisplayName("신고 사유가 비어있으면 400")
        void reportReview_EmptyReason_BadRequest() throws Exception {
                // given
                ReportReviewRequest request = new ReportReviewRequest("");

                // when & then
                mockMvc.perform(post("/api/reviews/1/report")
                                .with(user(userDetail))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }
}
