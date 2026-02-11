-- Add user profile and body measurement tables

-- User profile table (1:1 with users)
CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id),
    birth_year INTEGER,
    gender VARCHAR(20),
    unit_preference VARCHAR(10) NOT NULL DEFAULT 'IMPERIAL',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Body measurement table (many:1 with users)
-- All measurements stored in metric (cm, kg)
CREATE TABLE body_measurement (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Core stats
    height_cm DOUBLE PRECISION,
    weight_kg DOUBLE PRECISION,
    body_fat_percent DOUBLE PRECISION,

    -- Upper body
    neck_cm DOUBLE PRECISION,
    shoulders_cm DOUBLE PRECISION,
    chest_cm DOUBLE PRECISION,
    biceps_cm DOUBLE PRECISION,
    forearms_cm DOUBLE PRECISION,

    -- Core
    waist_cm DOUBLE PRECISION,
    hips_cm DOUBLE PRECISION,

    -- Lower body
    thighs_cm DOUBLE PRECISION,
    calves_cm DOUBLE PRECISION,

    -- Notes
    notes VARCHAR(500)
);

-- Indexes
CREATE INDEX idx_user_profile_user_id ON user_profile(user_id);
CREATE INDEX idx_body_measurement_user_id ON body_measurement(user_id);
CREATE INDEX idx_body_measurement_recorded_at ON body_measurement(recorded_at);
