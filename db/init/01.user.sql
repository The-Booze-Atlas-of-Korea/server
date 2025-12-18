CREATE TABLE users (
                       id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                       login_id VARCHAR(50) UNIQUE,               -- LOCAL 전용 로그인용 ID (NULL 허용)
                       password_hash VARCHAR(255),                -- LOCAL 전용 비밀번호 해시 (NULL 허용)
                       name VARCHAR(50) NOT NULL,
                       phone VARCHAR(20) NOT NULL,
                       email VARCHAR(100) NOT NULL,               -- 이메일은 공통 식별용
                       birth_date DATE NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       gender ENUM('M','F','OTHER','UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
                       profile_image_url VARCHAR(255),

                       auth_provider ENUM('LOCAL','GOOGLE') NOT NULL DEFAULT 'LOCAL',   -- 추가
                       provider_id VARCHAR(100) NULL,                                   -- 추가: GOOGLE의 sub 등

                       status ENUM('ACTIVE','WITHDRAWN','BANNED') NOT NULL DEFAULT 'ACTIVE',
                       visit_visibility_setting ENUM('PUBLIC','FRIENDS','PRIVATE') NOT NULL DEFAULT 'PRIVATE',
                       last_login_at DATETIME NULL,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       deleted_at DATETIME NULL,

                       UNIQUE KEY uk_users_provider (auth_provider, provider_id),  -- 동일 provider+id 중복 방지
                       INDEX idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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