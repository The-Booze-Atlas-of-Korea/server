package com.ssafy.sulmap.infra.model;

import java.util.Date;

public class BarEntity {
    ///     id                 BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    ///     name               VARCHAR(100)   NOT NULL,
    ///     address            VARCHAR(255)   NOT NULL,
    ///     latitude           DECIMAL(10, 7) NOT NULL,
    ///     longitude          DECIMAL(10, 7) NOT NULL,
    ///     base_category_name VARCHAR(50)    NULL,
    ///     open_information   TEXT           NULL,
    ///     menu               json           NULL,
    ///
    ///     created_at         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ///     updated_at         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ///     deleted_at         DATETIME       NULL,

    long id;
    String name;
    String address;
    String latitude;
    String longitude;
    String baseCategoryName;
    String openInformation;
    String menuJsonString;
    Date createAt;
    Date updateAt;
    Date deleteAt;

}
