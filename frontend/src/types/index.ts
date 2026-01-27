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
