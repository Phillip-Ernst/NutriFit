import api from './axios';
import type {
  WorkoutPlanRequest,
  WorkoutPlanResponse,
  WorkoutPlanDay,
  PredefinedExercise,
  ExerciseCategory,
  WorkoutLogFromPlanRequest,
  WorkoutLogResponse,
} from '../types';

export const createWorkoutPlan = async (
  data: WorkoutPlanRequest
): Promise<WorkoutPlanResponse> => {
  const response = await api.post<WorkoutPlanResponse>('/workout-plans', data);
  return response.data;
};

export const getMyWorkoutPlans = async (): Promise<WorkoutPlanResponse[]> => {
  const response = await api.get<WorkoutPlanResponse[]>('/workout-plans/mine');
  return response.data;
};

export const getWorkoutPlan = async (id: number): Promise<WorkoutPlanResponse> => {
  const response = await api.get<WorkoutPlanResponse>(`/workout-plans/${id}`);
  return response.data;
};

export const updateWorkoutPlan = async (
  id: number,
  data: WorkoutPlanRequest
): Promise<WorkoutPlanResponse> => {
  const response = await api.put<WorkoutPlanResponse>(`/workout-plans/${id}`, data);
  return response.data;
};

export const deleteWorkoutPlan = async (id: number): Promise<void> => {
  await api.delete(`/workout-plans/${id}`);
};

export const getWorkoutPlanDay = async (dayId: number): Promise<WorkoutPlanDay> => {
  const response = await api.get<WorkoutPlanDay>(`/workout-plans/days/${dayId}`);
  return response.data;
};

export const getPredefinedExercises = async (
  category?: ExerciseCategory
): Promise<PredefinedExercise[]> => {
  const params = category ? { category } : undefined;
  const response = await api.get<PredefinedExercise[]>('/exercises/predefined', { params });
  return response.data;
};

export const getExerciseCategories = async (): Promise<ExerciseCategory[]> => {
  const response = await api.get<ExerciseCategory[]>('/exercises/categories');
  return response.data;
};

export const createWorkoutFromPlan = async (
  data: WorkoutLogFromPlanRequest
): Promise<WorkoutLogResponse> => {
  const response = await api.post<WorkoutLogResponse>('/workouts/from-plan', data);
  return response.data;
};
