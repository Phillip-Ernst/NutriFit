-- Add UUID column to workout_plan_day for proper Set equality handling
ALTER TABLE workout_plan_day ADD COLUMN IF NOT EXISTS uuid VARCHAR(36);

-- Populate existing rows with UUIDs
UPDATE workout_plan_day SET uuid = gen_random_uuid()::text WHERE uuid IS NULL;

-- Make the column non-nullable and unique
ALTER TABLE workout_plan_day ALTER COLUMN uuid SET NOT NULL;
ALTER TABLE workout_plan_day ADD CONSTRAINT workout_plan_day_uuid_unique UNIQUE (uuid);
