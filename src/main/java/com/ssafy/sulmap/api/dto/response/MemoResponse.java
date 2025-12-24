package com.ssafy.sulmap.api.dto.response;

import com.ssafy.sulmap.core.model.MemoModel;

import java.time.LocalDateTime;

/**
 * 메모 응답 DTO
 */
public record MemoResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static MemoResponse fromModel(MemoModel model) {
        return new MemoResponse(
                model.getId(),
                model.getContent(),
                model.getCreatedAt(),
                model.getUpdatedAt());
    }
}
