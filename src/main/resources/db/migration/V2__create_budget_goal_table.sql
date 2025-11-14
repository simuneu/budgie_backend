CREATE TABLE budget_goal (
    goal_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    year INT NOT NULL,
    month INT NOT NULL,
    goal_amount BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    PRIMARY KEY (goal_id)
);

ALTER TABLE budget_goal
ADD CONSTRAINT fk_budgetgoal_user
FOREIGN KEY (user_id) REFERENCES `user`(user_id) ON DELETE CASCADE;