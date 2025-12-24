package com.ssafy.sulmap.core.model.command;

import lombok.Builder;

/**
 * 리뷰 신고 커맨드
 */
@Builder
public record ReportReviewCommand(
        Long reviewId,
        Long reporterId,
        String reason) {
}
