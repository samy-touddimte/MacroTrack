import { useQuery } from '@tanstack/react-query';
import { fetchMultiScenarioProjection } from '../services/analyticsApi';
import type { MultiScenarioProjectionResponse } from '../types';

export const useMultiScenarioProjection = (enabled: boolean = true) => {
  return useQuery<MultiScenarioProjectionResponse>({
    queryKey: ['multi-scenario-projection'],
    queryFn: fetchMultiScenarioProjection,
    staleTime: 5 * 60 * 1000,
    enabled,
    retry: false,
  });
};
