CREATE TABLE bar_category_mapping
(
    id          BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    bar_id      BIGINT UNSIGNED   NOT NULL,
    category_id BIGINT UNSIGNED   NOT NULL,
    priority    INT NULL,

    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME NULL,

    CONSTRAINT fk_bcm_bar FOREIGN KEY (bar_id) REFERENCES bars (id),
    CONSTRAINT fk_bcm_category FOREIGN KEY (category_id) REFERENCES bar_categories (id),

    UNIQUE KEY uk_bcm_bar_category (bar_id, category_id),
    INDEX       idx_bcm_bar_id (bar_id),
    INDEX       idx_bcm_category_id (category_id),
    INDEX       idx_bcm_deleted_at (deleted_at)
);