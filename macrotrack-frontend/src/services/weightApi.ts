import api from './axiosInstance';
import type { WeightEntry, PaginatedResponse } from '../types';

export const addWeightEntry = async (payload: { date: string; weightKg: number; bodyFatPercentage?: number }): Promise<WeightEntry> => {
  const response = await api.post<WeightEntry>('/weight-entries', payload);
  return response.data;
};

export const getWeightEntries = async (from: string, to: string): Promise<WeightEntry[]> => {
  const response = await api.get<PaginatedResponse<WeightEntry>>('/weight-entries', {
    params: { from, to, size: 500 },
  });
  return response.data.content ?? response.data;
};

export const deleteWeightEntry = async (id: number): Promise<void> => {
  await api.delete(`/weight-entries/${id}`);
};


