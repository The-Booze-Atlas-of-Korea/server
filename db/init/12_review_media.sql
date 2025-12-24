CREATE TABLE review_media
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    review_id   BIGINT UNSIGNED NOT NULL COMMENT '리뷰 ID',
    media_type  ENUM('IMAGE', 'VIDEO') NOT NULL COMMENT '미디어 타입',
    url         VARCHAR(512) NOT NULL COMMENT '미디어 URL',
    order_index INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '정렬 순서',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',

    CONSTRAINT review_media_review_fk FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE CASCADE,

    INDEX idx_review_media_review_order (review_id, order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰 미디어 테이블';
