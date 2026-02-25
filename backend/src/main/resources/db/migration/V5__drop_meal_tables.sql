-- Meal log tables have moved to nutrition-service (nutrifit_nutrition database).
-- Drop them from the main database.
DROP TABLE IF EXISTS meal_log_foods;
DROP TABLE IF EXISTS meal_log;
