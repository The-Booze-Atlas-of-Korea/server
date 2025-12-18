CREATE TABLE bars
(
    id                 BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name               VARCHAR(100)   NOT NULL,
    address            VARCHAR(255)   NOT NULL,
    latitude           DECIMAL(10, 7) NOT NULL,
    longitude          DECIMAL(10, 7) NOT NULL,
    base_category_name VARCHAR(50)    NULL,
    open_information   TEXT           NULL,
    menu               json           NULL,

    created_at         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at         DATETIME       NULL,

    INDEX idx_bars_lat_lng (latitude, longitude),
    INDEX idx_bars_deleted_at (deleted_at)
);



