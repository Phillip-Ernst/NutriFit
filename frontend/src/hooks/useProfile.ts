import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getProfile,
  updateProfile,
  createMeasurement,
  getMeasurements,
  getLatestMeasurement,
  deleteMeasurement,
  getChangeHistory,
} from '../api/profile';
import type { ProfileUpdateRequest, MeasurementRequest } from '../types';

// Profile hooks
export function useProfile() {
  return useQuery({
    queryKey: ['profile'],
    queryFn: getProfile,
  });
}

export function useUpdateProfile() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: ProfileUpdateRequest) => updateProfile(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['profile'] });
      queryClient.invalidateQueries({ queryKey: ['profile', 'history'] });
    },
  });
}

// Measurement hooks
export function useMeasurements() {
  return useQuery({
    queryKey: ['measurements'],
    queryFn: getMeasurements,
  });
}

export function useLatestMeasurement() {
  return useQuery({
    queryKey: ['measurements', 'latest'],
    queryFn: getLatestMeasurement,
  });
}

export function useCreateMeasurement() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: MeasurementRequest) => createMeasurement(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['measurements'] });
      queryClient.invalidateQueries({ queryKey: ['profile', 'history'] });
    },
  });
}

export function useDeleteMeasurement() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteMeasurement(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['measurements'] });
      queryClient.invalidateQueries({ queryKey: ['profile', 'history'] });
    },
  });
}

// Change history hook
export function useChangeHistory() {
  return useQuery({
    queryKey: ['profile', 'history'],
    queryFn: getChangeHistory,
  });
}
