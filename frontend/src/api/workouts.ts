import api from './axios';
import type { WorkoutLogRequest, WorkoutLogResponse } from '../types';

export const createWorkout = async (data: WorkoutLogRequest): Promise<WorkoutLogResponse> => {
  const response = await api.post<WorkoutLogResponse>('/workouts', data);
  return response.data;
};

export const getMyWorkouts = async (): Promise<WorkoutLogResponse[]> => {
  const response = await api.get<WorkoutLogResponse[]>('/workouts/mine');
  return response.data;
};
