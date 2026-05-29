import api from './axiosInstance';
import type { FoodLog, PaginatedResponse } from '../types';

export const addFoodLog = async (payload: Omit<FoodLog, 'id' | 'createdAt'>): Promise<FoodLog> => {
  const response = await api.post<FoodLog>('/food-logs', payload);
  return response.data;
};

export const getFoodLogsByDate = async (date: string): Promise<FoodLog[]> => {
  const response = await api.get<FoodLog[]>('/food-logs', {
    params: { date },
  });
  return response.data;
};

export const getFoodLogs = async (from: string, to: string): Promise<FoodLog[]> => {
  const response = await api.get<PaginatedResponse<FoodLog>>('/food-logs', {
    params: { from, to, size: 500 },
  });
  return response.data.content ?? response.data;
};

export const deleteFoodLog = async (id: number): Promise<void> => {
  await api.delete(`/food-logs/${id}`);
};


