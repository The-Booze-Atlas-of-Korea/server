package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.ReviewModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 목록 항목 응답 DTO
 */
public record ReviewListItemResponse(
        Long id,
        Long userId,
        Integer rating,
        String content,
        List<String> mediaUrls,
        LocalDateTime createdAt) {
    public static ReviewListItemResponse fromModel(ReviewModel model) {
        List<String> mediaUrls = model.getMedia() != null
                ? model.getMedia().stream()
                        .map(media -> media.getUrl())
                        .collect(Collectors.toList())
                : List.of();

        return new ReviewListItemResponse(
                model.getId(),
                model.getUserId(),
                model.getRating(),
                model.getContent(),
                mediaUrls,
                model.getCreatedAt());
    }
}
