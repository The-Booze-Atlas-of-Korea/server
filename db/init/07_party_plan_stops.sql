-- DRINKING_PLAN_SPOT (Party Plan Stops)
-- 술자리 계획의 각 방문 장소 테이블
CREATE TABLE drinking_plan_spot
(
    id                      BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    plan_id                 BIGINT UNSIGNED  NOT NULL,
    place_id                BIGINT UNSIGNED  NULL,                     -- bars.id 참조 (선택적)
    place_name_snapshot     VARCHAR(100)     NOT NULL,
    place_address_snapshot  VARCHAR(255)     NOT NULL,
    latitude                DECIMAL(10, 7)   NOT NULL,
    longitude               DECIMAL(10, 7)   NOT NULL,
    sequence                INT UNSIGNED     NOT NULL,                 -- 방문 순서
    memo                    TEXT             NULL,
    created_at              DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at              DATETIME         NULL,

    CONSTRAINT fk_drinking_plan_spot_plan
        FOREIGN KEY (plan_id) REFERENCES drinking_plan (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    CONSTRAINT fk_drinking_plan_spot_place
        FOREIGN KEY (place_id) REFERENCES bars (id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

    INDEX idx_drinking_plan_spot_plan (plan_id),
    INDEX idx_drinking_plan_spot_place (place_id),
    INDEX idx_drinking_plan_spot_sequence (plan_id, sequence),
    INDEX idx_drinking_plan_spot_deleted_at (deleted_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
