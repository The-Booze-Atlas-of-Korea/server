package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 리뷰 신고 요청 DTO
 */
public record ReportReviewRequest(
        @NotBlank(message = "신고 사유는 필수입니다.") String reason) {
}
