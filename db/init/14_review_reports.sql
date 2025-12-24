CREATE TABLE review_reports
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    review_id   BIGINT UNSIGNED NOT NULL COMMENT '신고된 리뷰 ID',
    reporter_id BIGINT UNSIGNED NOT NULL COMMENT '신고자 ID',
    reason      TEXT NOT NULL COMMENT '신고 사유',
    status      ENUM('PENDING', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '처리 상태',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '신고 시간',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    CONSTRAINT review_reports_review_fk FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,
    CONSTRAINT review_reports_reporter_fk FOREIGN KEY (reporter_id) REFERENCES users (id) ON DELETE CASCADE,

    INDEX idx_review_reports_review (review_id, created_at),
    INDEX idx_review_reports_reporter (reporter_id, created_at),
    INDEX idx_review_reports_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰 신고 테이블';
