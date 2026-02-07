import { describe, it, expect, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useMyMeals, useCreateMeal } from './useMeals';
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

describe('useMeals hooks', () => {
  describe('useMyMeals', () => {
    it('fetches meals successfully', async () => {
      const { result } = renderHook(() => useMyMeals(), {
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

    it('returns meals with expected structure', async () => {
      const { result } = renderHook(() => useMyMeals(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      const meal = result.current.data![0];
      expect(meal).toHaveProperty('id');
      expect(meal).toHaveProperty('createdAt');
      expect(meal).toHaveProperty('totalCalories');
      expect(meal).toHaveProperty('totalProtein');
      expect(meal).toHaveProperty('totalCarbs');
      expect(meal).toHaveProperty('totalFats');
      expect(meal).toHaveProperty('foods');
    });

    it('returns meals with foods array', async () => {
      const { result } = renderHook(() => useMyMeals(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      const meal = result.current.data![0];
      expect(Array.isArray(meal.foods)).toBe(true);
      expect(meal.foods.length).toBeGreaterThan(0);
    });

    it('returns food items with correct structure', async () => {
      const { result } = renderHook(() => useMyMeals(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      const food = result.current.data![0].foods[0];
      expect(food).toHaveProperty('type');
      expect(food).toHaveProperty('calories');
      expect(food).toHaveProperty('protein');
      expect(food).toHaveProperty('carbs');
      expect(food).toHaveProperty('fats');
    });

    it('uses correct query key', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      renderHook(() => useMyMeals(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      await waitFor(() => {
        const queries = queryClient.getQueryCache().getAll();
        expect(queries.some((q) =>
          JSON.stringify(q.queryKey) === JSON.stringify(['meals', 'mine'])
        )).toBe(true);
      });
    });
  });

  describe('useCreateMeal', () => {
    it('creates a meal mutation', async () => {
      const { result } = renderHook(() => useCreateMeal(), {
        wrapper: createWrapper(),
      });

      expect(result.current.mutate).toBeDefined();
      expect(typeof result.current.mutate).toBe('function');
    });

    it('mutation succeeds with valid data', async () => {
      const { result } = renderHook(() => useCreateMeal(), {
        wrapper: createWrapper(),
      });

      result.current.mutate({
        foods: [
          {
            type: 'Test Food',
            calories: 100,
            protein: 10,
            carbs: 15,
            fats: 3,
          },
        ],
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(result.current.data!.foods[0].type).toBe('Test Food');
    });

    it('calculates totals correctly', async () => {
      const { result } = renderHook(() => useCreateMeal(), {
        wrapper: createWrapper(),
      });

      result.current.mutate({
        foods: [
          {
            type: 'Food 1',
            calories: 200,
            protein: 20,
            carbs: 25,
            fats: 8,
          },
          {
            type: 'Food 2',
            calories: 150,
            protein: 10,
            carbs: 20,
            fats: 5,
          },
        ],
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data!.totalCalories).toBe(350);
      expect(result.current.data!.totalProtein).toBe(30);
      expect(result.current.data!.totalCarbs).toBe(45);
      expect(result.current.data!.totalFats).toBe(13);
    });

    it('invalidates meals query on success', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      // Spy on invalidateQueries to verify it's called
      const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');

      // First, populate the meals query
      const { result: mealsResult } = renderHook(() => useMyMeals(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      await waitFor(() => {
        expect(mealsResult.current.isSuccess).toBe(true);
      });

      // Now create a meal
      const { result: createResult } = renderHook(() => useCreateMeal(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      createResult.current.mutate({
        foods: [
          {
            type: 'New Meal',
            calories: 300,
            protein: 25,
            carbs: 30,
            fats: 10,
          },
        ],
      });

      await waitFor(() => {
        expect(createResult.current.isSuccess).toBe(true);
      });

      // Check that invalidateQueries was called with the correct query key
      expect(invalidateSpy).toHaveBeenCalledWith({
        queryKey: ['meals', 'mine'],
      });
    });

    it('handles null nutrient values', async () => {
      const { result } = renderHook(() => useCreateMeal(), {
        wrapper: createWrapper(),
      });

      result.current.mutate({
        foods: [
          {
            type: 'Unknown Food',
            calories: null,
            protein: null,
            carbs: null,
            fats: null,
          },
        ],
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data!.totalCalories).toBe(0);
      expect(result.current.data!.totalProtein).toBe(0);
    });
  });
});
