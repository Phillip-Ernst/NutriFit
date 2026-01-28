import api from './axios';
import type { LoginRequest, RegisterRequest, User } from '../types';

export const loginUser = async (data: LoginRequest): Promise<string> => {
  const response = await api.post<string>('/login', data, {
    transformResponse: [(data) => data],
  });
  return response.data;
};

export const registerUser = async (data: RegisterRequest): Promise<User> => {
  const response = await api.post<User>('/register', data);
  return response.data;
};
