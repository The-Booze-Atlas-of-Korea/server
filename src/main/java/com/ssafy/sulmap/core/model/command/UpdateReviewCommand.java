package com.ssafy.sulmap.core.model.command;

import lombok.Builder;

import java.util.List;

/**
 * 리뷰 수정 커맨드
 */
@Builder
public record UpdateReviewCommand(
        Long reviewId,
        Long userId,
        Integer rating,
        String content,
        List<String> mediaUrls) {
}
