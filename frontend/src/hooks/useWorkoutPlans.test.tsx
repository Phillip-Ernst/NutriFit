import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import {
  useMyWorkoutPlans,
  useWorkoutPlan,
  useCreateWorkoutPlan,
  usePredefinedExercises,
  useExerciseCategories,
} from './useWorkoutPlans';

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });
  return function Wrapper({ children }: { children: ReactNode }) {
    return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
  };
};

describe('useWorkoutPlans hooks', () => {
  describe('useMyWorkoutPlans', () => {
    it('should fetch user workout plans', async () => {
      const { result } = renderHook(() => useMyWorkoutPlans(), {
        wrapper: createWrapper(),
      });

      expect(result.current.isLoading).toBe(true);

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(result.current.data).toBeDefined();
      expect(result.current.data?.length).toBeGreaterThan(0);
      expect(result.current.data?.[0]).toHaveProperty('name');
      expect(result.current.data?.[0]).toHaveProperty('days');
    });
  });

  describe('useWorkoutPlan', () => {
    it('should fetch single workout plan by id', async () => {
      const { result } = renderHook(() => useWorkoutPlan(1), {
        wrapper: createWrapper(),
      });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(result.current.data).toBeDefined();
      expect(result.current.data?.id).toBe(1);
      expect(result.current.data?.name).toBe('PPL Split');
      expect(result.current.data?.days.length).toBe(3);
    });

    it('should not fetch when id is undefined', () => {
      const { result } = renderHook(() => useWorkoutPlan(undefined), {
        wrapper: createWrapper(),
      });

      expect(result.current.isLoading).toBe(false);
      expect(result.current.data).toBeUndefined();
    });
  });

  describe('useCreateWorkoutPlan', () => {
    it('should create a new workout plan', async () => {
      const { result } = renderHook(() => useCreateWorkoutPlan(), {
        wrapper: createWrapper(),
      });

      result.current.mutate({
        name: 'Test Plan',
        description: 'A test plan',
        days: [
          {
            dayNumber: 1,
            dayName: 'Day 1',
            exercises: [
              {
                name: 'Bench Press',
                category: 'CHEST',
                isCustom: false,
                targetSets: 3,
                targetReps: 10,
                targetWeight: 135,
              },
            ],
          },
        ],
      });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(result.current.data).toBeDefined();
      expect(result.current.data?.name).toBe('Test Plan');
      expect(result.current.data?.days.length).toBe(1);
    });
  });

  describe('usePredefinedExercises', () => {
    it('should fetch all predefined exercises', async () => {
      const { result } = renderHook(() => usePredefinedExercises(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(result.current.data).toBeDefined();
      expect(result.current.data?.length).toBeGreaterThan(0);
      expect(result.current.data?.[0]).toHaveProperty('id');
      expect(result.current.data?.[0]).toHaveProperty('name');
      expect(result.current.data?.[0]).toHaveProperty('category');
    });

    it('should filter exercises by category', async () => {
      const { result } = renderHook(() => usePredefinedExercises('CHEST'), {
        wrapper: createWrapper(),
      });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(result.current.data).toBeDefined();
      expect(result.current.data?.every((e) => e.category === 'CHEST')).toBe(true);
    });
  });

  describe('useExerciseCategories', () => {
    it('should fetch exercise categories', async () => {
      const { result } = renderHook(() => useExerciseCategories(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => expect(result.current.isSuccess).toBe(true));

      expect(result.current.data).toBeDefined();
      expect(result.current.data?.length).toBe(12);
      expect(result.current.data).toContain('CHEST');
      expect(result.current.data).toContain('BACK');
      expect(result.current.data).toContain('CARDIO');
    });
  });
});
