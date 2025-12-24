package com.ssafy.sulmap.core.model.command;

import lombok.Builder;

import java.util.List;

/**
 * 리뷰 생성 커맨드
 */
@Builder
public record CreateReviewCommand(
        Long userId,
        Long barId,
        Long visitId,
        Integer rating,
        String content,
        List<String> mediaUrls) {
}
