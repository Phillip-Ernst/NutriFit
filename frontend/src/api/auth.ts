import api from './axios';
import type { LoginRequest, LoginResponse, RegisterRequest, User } from '../types';

export const loginUser = async (data: LoginRequest): Promise<string> => {
  const response = await api.post<LoginResponse>('/login', data);
  return response.data.token;
};

export const registerUser = async (data: RegisterRequest): Promise<User> => {
  const response = await api.post<User>('/register', data);
  return response.data;
};
