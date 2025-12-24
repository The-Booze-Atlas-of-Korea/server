CREATE TABLE reviews
(
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT UNSIGNED NOT NULL COMMENT '리뷰 작성자 ID',
    bar_id     BIGINT UNSIGNED NOT NULL COMMENT '술집 ID',
    visit_id   BIGINT UNSIGNED NULL COMMENT '방문 ID (nullable)',
    rating     TINYINT UNSIGNED NOT NULL COMMENT '별점 (1-5)',
    content    TEXT NOT NULL COMMENT '리뷰 내용',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    deleted_at DATETIME NULL COMMENT 'Soft delete 시간',

    CONSTRAINT reviews_rating_check CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT reviews_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT reviews_bar_fk FOREIGN KEY (bar_id) REFERENCES bars (id) ON DELETE CASCADE,

    INDEX idx_reviews_bar_created (bar_id, created_at),
    INDEX idx_reviews_user_created (user_id, created_at),
    INDEX idx_reviews_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='리뷰 테이블';