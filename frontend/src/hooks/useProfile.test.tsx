import { describe, it, expect, vi } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  useProfile,
  useUpdateProfile,
  useMeasurements,
  useLatestMeasurement,
  useCreateMeasurement,
  useDeleteMeasurement,
} from './useProfile';
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

describe('useProfile hooks', () => {
  describe('useProfile', () => {
    it('fetches profile successfully', async () => {
      const { result } = renderHook(() => useProfile(), {
        wrapper: createWrapper(),
      });

      expect(result.current.isLoading).toBe(true);

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(result.current.data).toHaveProperty('username');
      expect(result.current.data).toHaveProperty('unitPreference');
    });

    it('uses correct query key', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      renderHook(() => useProfile(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      await waitFor(() => {
        const queries = queryClient.getQueryCache().getAll();
        expect(queries.some((q) => JSON.stringify(q.queryKey) === JSON.stringify(['profile']))).toBe(
          true
        );
      });
    });
  });

  describe('useUpdateProfile', () => {
    it('creates a mutation function', async () => {
      const { result } = renderHook(() => useUpdateProfile(), {
        wrapper: createWrapper(),
      });

      expect(result.current.mutate).toBeDefined();
      expect(typeof result.current.mutate).toBe('function');
    });

    it('mutation succeeds with valid data', async () => {
      const { result } = renderHook(() => useUpdateProfile(), {
        wrapper: createWrapper(),
      });

      result.current.mutate({
        birthYear: 1992,
        gender: 'MALE',
        unitPreference: 'METRIC',
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(result.current.data!.unitPreference).toBe('METRIC');
    });

    it('invalidates profile query on success', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');

      const { result: profileResult } = renderHook(() => useProfile(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      await waitFor(() => {
        expect(profileResult.current.isSuccess).toBe(true);
      });

      const { result: updateResult } = renderHook(() => useUpdateProfile(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      updateResult.current.mutate({ unitPreference: 'IMPERIAL' });

      await waitFor(() => {
        expect(updateResult.current.isSuccess).toBe(true);
      });

      expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ['profile'] });
    });
  });
});

describe('useMeasurements hooks', () => {
  describe('useMeasurements', () => {
    it('fetches measurements successfully', async () => {
      const { result } = renderHook(() => useMeasurements(), {
        wrapper: createWrapper(),
      });

      expect(result.current.isLoading).toBe(true);

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(Array.isArray(result.current.data)).toBe(true);
    });

    it('returns measurements with expected structure', async () => {
      const { result } = renderHook(() => useMeasurements(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      const measurement = result.current.data![0];
      expect(measurement).toHaveProperty('id');
      expect(measurement).toHaveProperty('recordedAt');
      expect(measurement).toHaveProperty('heightCm');
      expect(measurement).toHaveProperty('weightKg');
    });

    it('uses correct query key', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      renderHook(() => useMeasurements(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      await waitFor(() => {
        const queries = queryClient.getQueryCache().getAll();
        expect(
          queries.some((q) => JSON.stringify(q.queryKey) === JSON.stringify(['measurements']))
        ).toBe(true);
      });
    });
  });

  describe('useLatestMeasurement', () => {
    it('fetches latest measurement', async () => {
      const { result } = renderHook(() => useLatestMeasurement(), {
        wrapper: createWrapper(),
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(result.current.data).toHaveProperty('id');
    });

    it('uses correct query key', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      renderHook(() => useLatestMeasurement(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      await waitFor(() => {
        const queries = queryClient.getQueryCache().getAll();
        expect(
          queries.some(
            (q) => JSON.stringify(q.queryKey) === JSON.stringify(['measurements', 'latest'])
          )
        ).toBe(true);
      });
    });
  });

  describe('useCreateMeasurement', () => {
    it('creates a measurement mutation', async () => {
      const { result } = renderHook(() => useCreateMeasurement(), {
        wrapper: createWrapper(),
      });

      expect(result.current.mutate).toBeDefined();
      expect(typeof result.current.mutate).toBe('function');
    });

    it('mutation succeeds with valid data', async () => {
      const { result } = renderHook(() => useCreateMeasurement(), {
        wrapper: createWrapper(),
      });

      result.current.mutate({
        heightCm: 180,
        weightKg: 82,
        bodyFatPercent: 14,
      });

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });

      expect(result.current.data).toBeDefined();
      expect(result.current.data!.heightCm).toBe(180);
      expect(result.current.data!.weightKg).toBe(82);
    });

    it('invalidates measurements query on success', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');

      const { result: measurementsResult } = renderHook(() => useMeasurements(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      await waitFor(() => {
        expect(measurementsResult.current.isSuccess).toBe(true);
      });

      const { result: createResult } = renderHook(() => useCreateMeasurement(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      createResult.current.mutate({
        weightKg: 79,
      });

      await waitFor(() => {
        expect(createResult.current.isSuccess).toBe(true);
      });

      expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ['measurements'] });
    });
  });

  describe('useDeleteMeasurement', () => {
    it('creates a delete mutation', async () => {
      const { result } = renderHook(() => useDeleteMeasurement(), {
        wrapper: createWrapper(),
      });

      expect(result.current.mutate).toBeDefined();
      expect(typeof result.current.mutate).toBe('function');
    });

    it('mutation succeeds', async () => {
      const { result } = renderHook(() => useDeleteMeasurement(), {
        wrapper: createWrapper(),
      });

      result.current.mutate(1);

      await waitFor(() => {
        expect(result.current.isSuccess).toBe(true);
      });
    });

    it('invalidates measurements query on success', async () => {
      const queryClient = new QueryClient({
        defaultOptions: { queries: { retry: false } },
      });

      const invalidateSpy = vi.spyOn(queryClient, 'invalidateQueries');

      const { result: deleteResult } = renderHook(() => useDeleteMeasurement(), {
        wrapper: ({ children }) => (
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        ),
      });

      deleteResult.current.mutate(1);

      await waitFor(() => {
        expect(deleteResult.current.isSuccess).toBe(true);
      });

      expect(invalidateSpy).toHaveBeenCalledWith({ queryKey: ['measurements'] });
    });
  });
});
