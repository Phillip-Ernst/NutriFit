// === Auth ===

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}

export interface User {
  id: number;
  username: string;
}

// === Meals ===

export interface FoodItem {
  type: string;
  calories: number | null;
  protein: number | null;
  carbs: number | null;
  fats: number | null;
}

export interface MealLogRequest {
  foods: FoodItem[];
}

export interface MealLogResponse {
  id: number;
  createdAt: string;
  totalCalories: number;
  totalProtein: number;
  totalCarbs: number;
  totalFats: number;
  foods: FoodItem[];
}

// === Workouts ===

export interface ExerciseItem {
  name: string;
  category: string | null;
  durationMinutes: number | null;
  sets: number | null;
  reps: number | null;
  weight: number | null;
  caloriesBurned: number | null;
}

export interface WorkoutLogRequest {
  exercises: ExerciseItem[];
}

export interface WorkoutLogResponse {
  id: number;
  createdAt: string;
  totalDurationMinutes: number;
  totalCaloriesBurned: number;
  totalSets: number;
  totalReps: number;
  workoutPlanDayId: number | null;
  workoutPlanDayName: string | null;
  exercises: ExerciseItem[];
}

// === Workout Plans ===

export type ExerciseCategory =
  | 'BACK'
  | 'CHEST'
  | 'BICEPS'
  | 'TRICEPS'
  | 'SHOULDERS'
  | 'HAMSTRINGS'
  | 'QUADS'
  | 'GLUTES'
  | 'CALVES'
  | 'CORE'
  | 'CARDIO'
  | 'OTHER';

export interface PredefinedExercise {
  id: string;
  name: string;
  category: ExerciseCategory;
}

export interface WorkoutPlanExercise {
  name: string;
  category: ExerciseCategory | null;
  isCustom: boolean;
  targetSets: number | null;
  targetReps: number | null;
  targetWeight: number | null;
}

export interface WorkoutPlanDay {
  id?: number;
  dayNumber: number;
  dayName: string;
  exercises: WorkoutPlanExercise[];
}

export interface WorkoutPlanRequest {
  name: string;
  description: string | null;
  days: Omit<WorkoutPlanDay, 'id'>[];
}

export interface WorkoutPlanResponse {
  id: number;
  name: string;
  description: string | null;
  createdAt: string;
  days: WorkoutPlanDay[];
}

export interface WorkoutLogFromPlanRequest {
  workoutPlanDayId: number;
  exercises: ExerciseItem[];
}
