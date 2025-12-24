package com.ssafy.sulmap.core.model.command;

import lombok.Builder;

/**
 * 메모 생성/수정 커맨드
 */
@Builder
public record UpsertMemoCommand(
        Long userId,
        Long barId,
        String content) {
}
