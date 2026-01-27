import api from './axios';
import type { MealLogRequest, MealLogResponse } from '../types';

export const createMeal = async (data: MealLogRequest): Promise<MealLogResponse> => {
  const response = await api.post<MealLogResponse>('/meals', data);
  return response.data;
};

export const getMyMeals = async (): Promise<MealLogResponse[]> => {
  const response = await api.get<MealLogResponse[]>('/meals/mine');
  return response.data;
};
