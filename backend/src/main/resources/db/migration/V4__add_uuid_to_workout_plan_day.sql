-- Add UUID column to workout_plan_day for proper Set equality handling
-- This migration is idempotent and safe to run multiple times

-- Step 1: Add the column if it doesn't exist (nullable initially)
ALTER TABLE workout_plan_day ADD COLUMN IF NOT EXISTS uuid VARCHAR(36);

-- Step 2: Populate any NULL values with generated UUIDs
UPDATE workout_plan_day SET uuid = gen_random_uuid()::text WHERE uuid IS NULL;

-- Step 3: Make the column non-nullable (only if not already)
ALTER TABLE workout_plan_day ALTER COLUMN uuid SET NOT NULL;

-- Step 4: Add unique constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'workout_plan_day_uuid_unique'
    ) THEN
        ALTER TABLE workout_plan_day ADD CONSTRAINT workout_plan_day_uuid_unique UNIQUE (uuid);
    END IF;
END $$;
