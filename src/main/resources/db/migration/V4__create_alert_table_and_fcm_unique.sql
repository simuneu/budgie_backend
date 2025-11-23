CREATE TABLE alert (
    alert_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,

    CONSTRAINT fk_alert_user
        FOREIGN KEY (user_id) REFERENCES user(user_id)
        ON DELETE CASCADE
);

-- 인덱스 추가 (조회 속도 + 중복 체크 용도)
CREATE INDEX idx_alert_user_created_at
ON alert (user_id, created_at);

ALTER TABLE user
ADD COLUMN fcm_token VARCHAR(512);

DROP TABLE IF EXISTS fcm_token;
