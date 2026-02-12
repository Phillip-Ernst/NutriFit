import api from './axios';
import type {
  ProfileResponse,
  ProfileUpdateRequest,
  MeasurementRequest,
  MeasurementResponse,
  UserChangeHistoryResponse,
} from '../types';

// Profile endpoints
export const getProfile = async (): Promise<ProfileResponse> => {
  const response = await api.get<ProfileResponse>('/profile');
  return response.data;
};

export const updateProfile = async (data: ProfileUpdateRequest): Promise<ProfileResponse> => {
  const response = await api.put<ProfileResponse>('/profile', data);
  return response.data;
};

// Measurement endpoints
export const createMeasurement = async (data: MeasurementRequest): Promise<MeasurementResponse> => {
  const response = await api.post<MeasurementResponse>('/measurements', data);
  return response.data;
};

export const getMeasurements = async (): Promise<MeasurementResponse[]> => {
  const response = await api.get<MeasurementResponse[]>('/measurements');
  return response.data;
};

export const getLatestMeasurement = async (): Promise<MeasurementResponse | null> => {
  const response = await api.get<MeasurementResponse>('/measurements/latest');
  // 204 No Content returns empty data
  if (response.status === 204) {
    return null;
  }
  return response.data;
};

export const deleteMeasurement = async (id: number): Promise<void> => {
  await api.delete(`/measurements/${id}`);
};

// Change history endpoint
export const getChangeHistory = async (): Promise<UserChangeHistoryResponse[]> => {
  const response = await api.get<UserChangeHistoryResponse[]>('/profile/history');
  return response.data;
};
