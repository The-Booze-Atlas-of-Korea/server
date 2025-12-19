CREATE TABLE bar_categories
(
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    group_name VARCHAR(50) NULL,

    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,

    UNIQUE KEY uk_bar_categories_name (name),
    INDEX      idx_bar_categories_deleted_at (deleted_at)
);