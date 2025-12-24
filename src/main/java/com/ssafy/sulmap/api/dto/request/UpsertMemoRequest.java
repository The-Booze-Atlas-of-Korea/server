package com.ssafy.sulmap.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 메모 생성/수정 요청 DTO
 */
public record UpsertMemoRequest(
        @NotBlank(message = "메모 내용은 필수입니다.") String content) {
}
