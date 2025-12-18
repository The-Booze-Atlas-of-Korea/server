CREATE TABLE user_preference_profiles (
                                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                          user_id BIGINT UNSIGNED NOT NULL,
                                          preferred_categories JSON NULL,
                                          preferred_price_level ENUM('LOW','MID','HIGH') NULL,
                                          preferred_mood_tags JSON NULL,
                                          disliked_tags JSON NULL,
                                          profile_vector JSON NULL,
                                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          deleted_at DATETIME NULL,

                                          CONSTRAINT fk_upp_user
                                              FOREIGN KEY (user_id) REFERENCES users(id)
                                                  ON DELETE CASCADE
                                                  ON UPDATE CASCADE,
                                          UNIQUE KEY uk_upp_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;