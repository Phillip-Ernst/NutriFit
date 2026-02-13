// === Auth ===

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface User {
  id: number;
  username: string;
}

// === Meals ===

export interface FoodItem {
  id?: string;
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
  id?: string;
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
  id?: string;
  name: string;
  category: ExerciseCategory | null;
  isCustom: boolean;
  targetSets: number | null;
  targetReps: number | null;
  targetWeight: number | null;
}

export interface WorkoutPlanDay {
  id?: number;
  clientId?: string;
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

// === Profile ===

export type Gender = 'MALE' | 'FEMALE' | 'OTHER' | 'PREFER_NOT_TO_SAY';

export type UnitPreference = 'IMPERIAL' | 'METRIC';

export interface ProfileResponse {
  id: number;
  username: string;
  birthYear: number | null;
  age: number | null;
  gender: Gender | null;
  unitPreference: UnitPreference;
  createdAt: string;
  updatedAt: string;
}

export interface ProfileUpdateRequest {
  birthYear?: number | null;
  gender?: Gender | null;
  unitPreference?: UnitPreference;
}

// === Body Measurements ===

export interface MeasurementRequest {
  heightCm?: number | null;
  weightKg?: number | null;
  bodyFatPercent?: number | null;
  neckCm?: number | null;
  shouldersCm?: number | null;
  chestCm?: number | null;
  bicepsCm?: number | null;
  forearmsCm?: number | null;
  waistCm?: number | null;
  hipsCm?: number | null;
  thighsCm?: number | null;
  calvesCm?: number | null;
  notes?: string | null;
}

export interface MeasurementResponse {
  id: number;
  recordedAt: string;
  heightCm: number | null;
  weightKg: number | null;
  bodyFatPercent: number | null;
  neckCm: number | null;
  shouldersCm: number | null;
  chestCm: number | null;
  bicepsCm: number | null;
  forearmsCm: number | null;
  waistCm: number | null;
  hipsCm: number | null;
  thighsCm: number | null;
  calvesCm: number | null;
  notes: string | null;
}

// === Change History ===

export interface UserChangeHistoryResponse {
  id: number;
  entityType: 'PROFILE' | 'MEASUREMENT';
  entityId: number | null;
  fieldName: string;
  oldValue: string | null;
  newValue: string | null;
  changedAt: string;
}
