-- DRINKING_PLAN (Party Plans)
-- 술자리 계획 테이블
CREATE TABLE drinking_plan
(
    id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    owner_user_id BIGINT UNSIGNED                                    NOT NULL,
    title         VARCHAR(100)                                       NOT NULL,
    description   TEXT                                               NULL,
    theme         ENUM ('CASUAL','PARTY','DATE','BUSINESS','SPECIAL') NULL,
    total_budget  BIGINT                                             NULL,
    created_at    DATETIME                                           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME                                           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    DATETIME                                           NULL,

    CONSTRAINT fk_drinking_plan_owner
        FOREIGN KEY (owner_user_id) REFERENCES users (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    INDEX idx_drinking_plan_owner (owner_user_id),
    INDEX idx_drinking_plan_created_at (created_at),
    INDEX idx_drinking_plan_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
