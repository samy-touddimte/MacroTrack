import api from './axiosInstance';
import type {
  ProjectionData,
  TdeeData,
  WeightTrendData,
  DashboardData,
  MultiScenarioProjectionResponse,
  MetabolicForecastResponse,
} from '../types';

export const fetchWeightTrend = async (from: string, to: string): Promise<WeightTrendData> => {
  const response = await api.get<WeightTrendData>('/analytics/weight-trend', { params: { from, to } });
  return response.data;
};

export const fetchTdee = async (from: string, to: string): Promise<TdeeData> => {
  const response = await api.get<TdeeData>('/analytics/tdee', { params: { from, to } });
  return response.data;
};

export const fetchDashboard = async (): Promise<DashboardData> => {
  const response = await api.get<DashboardData>('/dashboard');
  return response.data;
};

export const fetchProjection = async (): Promise<ProjectionData> => {
  const response = await api.get<ProjectionData>('/dashboard/projection');
  return response.data;
};

export const fetchMultiScenarioProjection = async (): Promise<MultiScenarioProjectionResponse> => {
  const response = await api.get<MultiScenarioProjectionResponse>('/dashboard/multi-scenario-projection');
  return response.data;
};

export const fetchMetabolicForecast = async (params: {
  currentWeight: number;
  goalWeight: number;
  weeklyRate: number;
  startDate: string;
}): Promise<MetabolicForecastResponse> => {
  const response = await api.get<MetabolicForecastResponse>('/dashboard/metabolic-forecast', { params });
  return response.data;
};
