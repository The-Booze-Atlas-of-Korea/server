-- 3) VISITS
--  bar_id 는 BARS.id 를 FK로 가정 (BARS 테이블은 다른 도메인에서 생성)
CREATE TABLE visits
(
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT UNSIGNED                     NOT NULL,
    bar_id       BIGINT UNSIGNED                     NOT NULL,
    visited_at   DATETIME                            NOT NULL,
    people_count INT UNSIGNED                        NULL,
    phase        TINYINT UNSIGNED                    NULL,
    visibility   ENUM ('PUBLIC','FRIENDS','PRIVATE') NOT NULL,
    created_at   DATETIME                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME                            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at   DATETIME                            NULL,

    CONSTRAINT fk_visits_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    -- BARS 테이블이 같은 DB에 있을 때만 FK 걸기
    CONSTRAINT fk_visits_bar
        FOREIGN KEY (bar_id) REFERENCES bars (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,

    INDEX idx_visits_user (user_id),
    INDEX idx_visits_bar (bar_id),
    INDEX idx_visits_visited_at (visited_at),
    INDEX idx_visits_visibility (visibility)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;