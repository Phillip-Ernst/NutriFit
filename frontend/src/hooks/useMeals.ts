import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { createMeal, getMyMeals, deleteMeal } from '../api/meals';
import type { MealLogRequest } from '../types';

export function useMyMeals() {
  return useQuery({
    queryKey: ['meals', 'mine'],
    queryFn: getMyMeals,
  });
}

export function useCreateMeal() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: MealLogRequest) => createMeal(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['meals', 'mine'] });
    },
  });
}

export function useDeleteMeal() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteMeal(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['meals', 'mine'] });
    },
  });
}
