package com.ssafy.sulmap.core.model;

import com.ssafy.sulmap.core.model.enums.ReportStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 리뷰 신고 도메인 모델
 */
@Getter
@Setter
@Builder
public class ReviewReportModel {
    private Long id;
    private Long reviewId;
    private Long reporterId;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
