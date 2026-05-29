import api from './axiosInstance';
import type { AuthResponse, LoginRequest, RegisterRequest, ChangePasswordRequest } from '../types';

export const register = async (payload: RegisterRequest): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/register', payload);
  return response.data;
};

export const login = async (payload: LoginRequest): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/login', payload);
  return response.data;
};

export const refreshToken = async (refreshToken: string): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>('/auth/refresh', { refreshToken });
  return response.data;
};

export const logoutApi = async (refreshToken: string): Promise<void> => {
  await api.post('/auth/logout', { refreshToken });
};

export const changePassword = async (data: ChangePasswordRequest): Promise<void> => {
  await api.post('/auth/password', data);
};