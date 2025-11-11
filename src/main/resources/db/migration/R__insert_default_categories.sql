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
('EXP', '예적금'),
('INCOME', '월급'),
('INCOME', '용돈'),
('INCOME', '부수입'),
('INCOME', '기타')
ON DUPLICATE KEY UPDATE name=VALUES(name);

