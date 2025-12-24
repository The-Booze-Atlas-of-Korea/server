package com.ssafy.sulmap.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 개인 메모 도메인 모델
 */
@Getter
@Setter
@Builder
public class MemoModel {
    private Long id;
    private Long userId;
    private Long barId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
