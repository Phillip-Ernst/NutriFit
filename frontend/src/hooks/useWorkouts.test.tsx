import { describe, it, expect } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useMyWorkouts, useCreateWorkout } from './useWorkouts';
import type { ReactNode } from 'react';

function createWrapper() {
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
}

describe('useWorkouts hooks', () => {
  describe('useMyWorkouts', () => {
    it('fetches workouts successfully', async () => {
      const { result } = renderHook(() => useMyWorkouts(), {
        wrapper: createWrapper(),
      });

      expect(result.current.isLoading).toBe(true);

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(Array.isArray(result.current.data)).toBe(true);
      expect(result.current.data!.length).toBeGreaterThan(0);
    });

    it('returns workouts with expected structure', async () => {
      const { result } = renderHook(() => useMyWorkouts(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      const workout = result.current.data![0];
      expect(workout).toHaveProperty('id');
      expect(workout).toHaveProperty('createdAt');
      expect(workout).toHaveProperty('totalDurationMinutes');
      expect(workout).toHaveProperty('exercises');
    });
  });

  describe('useCreateWorkout', () => {
    it('creates a workout mutation', async () => {
      const { result } = renderHook(() => useCreateWorkout(), {
        wrapper: createWrapper(),
      });

      expect(result.current.mutate).toBeDefined();
      expect(typeof result.current.mutate).toBe('function');
    });

    it('mutation succeeds with valid data', async () => {
      const { result } = renderHook(() => useCreateWorkout(), {
        wrapper: createWrapper(),
      });

      result.current.mutate({
        exercises: [
          {
            name: 'Test Exercise',
            category: null,
            sets: 3,
            reps: 10,
            weight: null,
            durationMinutes: null,
            caloriesBurned: null,
          },
        ],
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(result.current.data!.exercises[0].name).toBe('Test Exercise');
    });
  });
});
