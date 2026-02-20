-- Per-set workout tracking
-- Allows tracking individual sets with different reps/weights per set
-- Adds a JSON column to workout_log_exercises for set details

ALTER TABLE workout_log_exercises ADD COLUMN set_details TEXT;
