CREATE TABLE memos
(
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT UNSIGNED NOT NULL COMMENT '사용자 ID',
    bar_id     BIGINT UNSIGNED NOT NULL COMMENT '술집 ID',
    content    TEXT NOT NULL COMMENT '메모 내용',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    deleted_at DATETIME NULL COMMENT 'Soft delete 시간',

    CONSTRAINT memos_user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT memos_bar_fk FOREIGN KEY (bar_id) REFERENCES bars (id) ON DELETE CASCADE,

    UNIQUE KEY uk_memos_user_bar (user_id, bar_id),
    INDEX idx_memos_deleted (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='개인 메모 테이블';