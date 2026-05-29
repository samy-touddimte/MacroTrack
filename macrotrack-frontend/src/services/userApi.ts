import api from './axiosInstance';
import type { User, UpdateUserRequest } from '../types';

export const fetchCurrentUser = async (): Promise<User> => {
  const response = await api.get('/users/me');
  return response.data;
};

export const updateCurrentUser = async (data: UpdateUserRequest): Promise<User> => {
  const response = await api.put('/users/me', data);
  return response.data;
};