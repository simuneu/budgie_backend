USE budgie;

CREATE TABLE IF NOT EXISTS `user` (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(20) NOT NULL,
    user_status VARCHAR(5) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS email_verification (
    email_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    email VARCHAR(255) NOT NULL UNIQUE,
    valid_code VARCHAR(50) NOT NULL,
    expiration_time DATETIME,
    is_verified VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_email_verification_user
        FOREIGN KEY (user_id) REFERENCES `user`(user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_token (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    token_value VARCHAR(500) NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_token_user
        FOREIGN KEY (user_id) REFERENCES `user`(user_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS category (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    budget_type VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category_type_name (budget_type, name)
);

CREATE TABLE IF NOT EXISTS transaction (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    budget_type VARCHAR(10) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    memo VARCHAR(255),
    transaction_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE
);

INSERT INTO category (budget_type, name)
VALUES
    ('EXP', '식비'),
    ('EXP', '주거비'),
    ('EXP', '교통'),
    ('EXP', '문화생활'),
    ('EXP', '생활비'),
    ('EXP', '건강'),
    ('EXP', '의료'),
    ('EXP', '교육'),
    ('EXP', '경조사비'),
    ('EXP', '기타'),
    ('INCOME', '월급'),
    ('INCOME', '용돈'),
    ('INCOME', '부수입'),
    ('INCOME', '예적금');
