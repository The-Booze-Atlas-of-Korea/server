-- PLAN_VOTES
-- 플랜 투표 테이블 (사용자가 특정 플랜에 대해 투표)
CREATE TABLE plan_votes
(
    id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    plan_id    BIGINT UNSIGNED                     NOT NULL,
    user_id    BIGINT UNSIGNED                     NOT NULL,
    vote_type  ENUM ('UP','DOWN','NEUTRAL')        NOT NULL DEFAULT 'NEUTRAL',
    created_at DATETIME                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME                            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME                            NULL,

    CONSTRAINT fk_plan_votes_plan
        FOREIGN KEY (plan_id) REFERENCES drinking_plan (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT fk_plan_votes_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    UNIQUE KEY uk_plan_votes_user_plan (plan_id, user_id),
    INDEX idx_plan_votes_plan (plan_id),
    INDEX idx_plan_votes_user (user_id),
    INDEX idx_plan_votes_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
