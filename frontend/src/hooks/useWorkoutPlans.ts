import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  createWorkoutPlan,
  getMyWorkoutPlans,
  getWorkoutPlan,
  updateWorkoutPlan,
  deleteWorkoutPlan,
  getWorkoutPlanDay,
  getPredefinedExercises,
  getExerciseCategories,
  createWorkoutFromPlan,
} from '../api/workoutPlans';
import type {
  WorkoutPlanRequest,
  ExerciseCategory,
  WorkoutLogFromPlanRequest,
} from '../types';

export function useMyWorkoutPlans() {
  return useQuery({
    queryKey: ['workoutPlans', 'mine'],
    queryFn: getMyWorkoutPlans,
  });
}

export function useWorkoutPlan(id: number | undefined) {
  return useQuery({
    queryKey: ['workoutPlans', id],
    queryFn: () => getWorkoutPlan(id!),
    enabled: !!id,
  });
}

export function useCreateWorkoutPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: WorkoutPlanRequest) => createWorkoutPlan(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workoutPlans', 'mine'] });
    },
  });
}

export function useUpdateWorkoutPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: WorkoutPlanRequest }) =>
      updateWorkoutPlan(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['workoutPlans', 'mine'] });
      queryClient.invalidateQueries({ queryKey: ['workoutPlans', variables.id] });
    },
  });
}

export function useDeleteWorkoutPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteWorkoutPlan(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workoutPlans', 'mine'] });
    },
  });
}

export function useWorkoutPlanDay(dayId: number | undefined) {
  return useQuery({
    queryKey: ['workoutPlanDays', dayId],
    queryFn: () => getWorkoutPlanDay(dayId!),
    enabled: !!dayId,
  });
}

export function usePredefinedExercises(category?: ExerciseCategory) {
  return useQuery({
    queryKey: ['predefinedExercises', category],
    queryFn: () => getPredefinedExercises(category),
  });
}

export function useExerciseCategories() {
  return useQuery({
    queryKey: ['exerciseCategories'],
    queryFn: getExerciseCategories,
  });
}

export function useCreateWorkoutFromPlan() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: WorkoutLogFromPlanRequest) => createWorkoutFromPlan(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['workouts', 'mine'] });
    },
  });
}
