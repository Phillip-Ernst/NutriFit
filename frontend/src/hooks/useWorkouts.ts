import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { createWorkout, getMyWorkouts, deleteWorkout } from '../api/workouts';
import type { WorkoutLogRequest } from '../types';

export function useMyWorkouts() {
  return useQuery({
    queryKey: ['workouts', 'mine'],
    queryFn: getMyWorkouts,
  });
}

export function useCreateWorkout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: WorkoutLogRequest) => createWorkout(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workouts', 'mine'] });
    },
  });
}

export function useDeleteWorkout() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteWorkout(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workouts', 'mine'] });
    },
  });
}
