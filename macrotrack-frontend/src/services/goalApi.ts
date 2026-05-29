import axios from 'axios';
import api from './axiosInstance';
import type { Goal } from '../types';

export const createGoal = async (payload: { targetWeightKg: number; weeklyRateKg: number; startDate: string | null; }): Promise<Goal> => {
  const response = await api.post<Goal>('/goals', payload);
  return response.data;
};

export const getActiveGoal = async (): Promise<Goal | null> => {
  try {
    const response = await api.get<Goal>('/goals/active');
    if (response.status === 204 || !response.data) {
      return null;
    }
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      return null;
    }
    throw error;
  }
};

export const updateGoal = async (id: number, payload: { targetWeightKg: number; weeklyRateKg: number; startDate: string | null; }): Promise<Goal> => {
  const response = await api.put<Goal>(`/goals/${id}`, payload);
  return response.data;
};

export const getGoalHistory = async (): Promise<Goal[]> => {
  const response = await api.get<Goal[]>('/goals/history');
  return response.data;
};

export const deleteGoal = async (id: number): Promise<void> => {
  await api.delete(`/goals/${id}`);
};
