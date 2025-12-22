-- DRINKING_SCHEDULE (Schedules)
-- 술자리 일정 테이블
CREATE TABLE drinking_schedule
(
    id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    owner_user_id  BIGINT UNSIGNED                              NOT NULL,
    plan_id        BIGINT UNSIGNED                              NULL,       -- 플랜 참조 (선택적)
    schedule_title VARCHAR(100)                                 NOT NULL,
    meet_at        DATETIME                                     NOT NULL,
    status         ENUM ('PLANNED','CONFIRMED','CANCELLED','COMPLETED') NOT NULL DEFAULT 'PLANNED',
    created_at     DATETIME                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at     DATETIME                                     NULL,

    CONSTRAINT fk_drinking_schedule_owner
        FOREIGN KEY (owner_user_id) REFERENCES users (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT fk_drinking_schedule_plan
        FOREIGN KEY (plan_id) REFERENCES drinking_plan (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

    INDEX idx_drinking_schedule_owner (owner_user_id),
    INDEX idx_drinking_schedule_plan (plan_id),
    INDEX idx_drinking_schedule_meet_at (meet_at),
    INDEX idx_drinking_schedule_status (status),
    INDEX idx_drinking_schedule_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
