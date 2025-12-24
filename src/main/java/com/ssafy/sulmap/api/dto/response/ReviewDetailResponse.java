package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.ReviewModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 리뷰 상세 응답 DTO
 * (MVP에서는 목록 응답과 동일)
 */
public record ReviewDetailResponse(
        Long id,
        Long userId,
        Integer rating,
        String content,
        List<String> mediaUrls,
        LocalDateTime createdAt) {
    public static ReviewDetailResponse fromModel(ReviewModel model) {
        List<String> mediaUrls = model.getMedia() != null
                ? model.getMedia().stream()
                        .map(media -> media.getUrl())
                        .collect(Collectors.toList())
                : List.of();

        return new ReviewDetailResponse(
                model.getId(),
                model.getUserId(),
                model.getRating(),
                model.getContent(),
                mediaUrls,
                model.getCreatedAt());
    }
}
