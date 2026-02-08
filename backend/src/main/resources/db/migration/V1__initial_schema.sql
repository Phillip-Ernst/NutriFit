-- NutriFit Initial Schema
-- This migration creates the initial database schema

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Meal log table
CREATE TABLE meal_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    total_calories INTEGER NOT NULL DEFAULT 0,
    total_protein INTEGER NOT NULL DEFAULT 0,
    total_carbs INTEGER NOT NULL DEFAULT 0,
    total_fats INTEGER NOT NULL DEFAULT 0
);

-- Meal log foods (element collection for MealLog.foods)
CREATE TABLE meal_log_foods (
    meal_log_id BIGINT NOT NULL REFERENCES meal_log(id) ON DELETE CASCADE,
    type VARCHAR(255) NOT NULL,
    calories INTEGER,
    protein INTEGER,
    carbs INTEGER,
    fats INTEGER
);

-- Workout plan table
CREATE TABLE workout_plan (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Workout plan day table
CREATE TABLE workout_plan_day (
    id BIGSERIAL PRIMARY KEY,
    workout_plan_id BIGINT NOT NULL REFERENCES workout_plan(id) ON DELETE CASCADE,
    day_number INTEGER NOT NULL,
    day_name VARCHAR(255) NOT NULL
);

-- Workout plan day exercises (element collection for WorkoutPlanDay.exercises)
CREATE TABLE workout_plan_day_exercises (
    workout_plan_day_id BIGINT NOT NULL REFERENCES workout_plan_day(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50),
    is_custom BOOLEAN NOT NULL DEFAULT FALSE,
    target_sets INTEGER,
    target_reps INTEGER,
    target_weight INTEGER
);

-- Workout log table
CREATE TABLE workout_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    workout_plan_day_id BIGINT REFERENCES workout_plan_day(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    total_duration_minutes INTEGER NOT NULL DEFAULT 0,
    total_calories_burned INTEGER NOT NULL DEFAULT 0,
    total_sets INTEGER NOT NULL DEFAULT 0,
    total_reps INTEGER NOT NULL DEFAULT 0
);

-- Workout log exercises (element collection for WorkoutLog.exercises)
CREATE TABLE workout_log_exercises (
    workout_log_id BIGINT NOT NULL REFERENCES workout_log(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    duration_minutes INTEGER,
    sets INTEGER,
    reps INTEGER,
    weight INTEGER,
    calories_burned INTEGER
);

-- Indexes for foreign keys and common queries
CREATE INDEX idx_meal_log_user_id ON meal_log(user_id);
CREATE INDEX idx_meal_log_created_at ON meal_log(created_at);
CREATE INDEX idx_workout_plan_user_id ON workout_plan(user_id);
CREATE INDEX idx_workout_plan_day_workout_plan_id ON workout_plan_day(workout_plan_id);
CREATE INDEX idx_workout_log_user_id ON workout_log(user_id);
CREATE INDEX idx_workout_log_created_at ON workout_log(created_at);
