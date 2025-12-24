package com.ssafy.sulmap.infra.model;

import com.ssafy.sulmap.core.model.ReviewReportModel;
import com.ssafy.sulmap.core.model.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 신고 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportEntity {
    private Long id;
    private Long reviewId;
    private Long reporterId;
    private String reason;
    private String status; // DB에서는 문자열로 저장
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 엔티티 → 모델 변환
     */
    public ReviewReportModel toModel() {
        return ReviewReportModel.builder()
                .id(id)
                .reviewId(reviewId)
                .reporterId(reporterId)
                .reason(reason)
                .status(ReportStatus.valueOf(status))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    /**
     * 모델 → 엔티티 변환
     */
    public static ReviewReportEntity fromModel(ReviewReportModel model) {
        return ReviewReportEntity.builder()
                .id(model.getId())
                .reviewId(model.getReviewId())
                .reporterId(model.getReporterId())
                .reason(model.getReason())
                .status(model.getStatus() != null ? model.getStatus().name() : ReportStatus.PENDING.name())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
