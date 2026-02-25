CREATE TABLE meal_log (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    total_calories INTEGER NOT NULL DEFAULT 0,
    total_protein INTEGER NOT NULL DEFAULT 0,
    total_carbs INTEGER NOT NULL DEFAULT 0,
    total_fats INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE meal_log_foods (
    meal_log_id BIGINT NOT NULL REFERENCES meal_log(id) ON DELETE CASCADE,
    type VARCHAR(255) NOT NULL,
    calories INTEGER,
    protein INTEGER,
    carbs INTEGER,
    fats INTEGER
);

CREATE INDEX idx_meal_log_username ON meal_log(username);
CREATE INDEX idx_meal_log_created_at ON meal_log(created_at);