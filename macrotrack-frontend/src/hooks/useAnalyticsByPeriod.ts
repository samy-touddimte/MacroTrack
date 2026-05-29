import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { format, subDays, subMonths, subYears } from 'date-fns';
import { fetchWeightTrend, fetchTdee } from '../services/analyticsApi';
import type { TdeeData, WeightTrendData } from '../types';

// périodes disponibles dans le sélecteur
export type Period = '1S' | '1M' | '3M' | '6M' | '1A' | 'TOUT';

// calcule la date de début selon la période choisie
const getStartDate = (period: Period): string => {
  const today = new Date();
  switch (period) {
    case '1S': return format(subDays(today, 7), 'yyyy-MM-dd');
    case '1M': return format(subMonths(today, 1), 'yyyy-MM-dd');
    case '3M': return format(subMonths(today, 3), 'yyyy-MM-dd');
    case '6M': return format(subMonths(today, 6), 'yyyy-MM-dd');
    case '1A': return format(subYears(today, 1), 'yyyy-MM-dd');
    case 'TOUT': return format(subYears(today, 5), 'yyyy-MM-dd');
  }
};

export const useAnalyticsByPeriod = (period: Period) => {
  const endDate = format(new Date(), 'yyyy-MM-dd');
  const startDate = useMemo(() => getStartDate(period), [period]);

  const {
    data: weightTrend,
    isLoading: isWeightLoading,
    error: weightError,
  } = useQuery<WeightTrendData>({
    queryKey: ['weight-trend', startDate, endDate],
    queryFn: () => fetchWeightTrend(startDate, endDate),
    staleTime: 5 * 60 * 1000,
  });

  const {
    data: tdeeData,
    isLoading: isTdeeLoading,
    error: tdeeError,
  } = useQuery<TdeeData>({
    queryKey: ['tdee', startDate, endDate],
    queryFn: () => fetchTdee(startDate, endDate),
    staleTime: 5 * 60 * 1000,
  });

  const isLoading = isWeightLoading || isTdeeLoading;
  const firstError = weightError || tdeeError;
  const error = firstError instanceof Error ? firstError.message : null;

  return {
    weightTrend: weightTrend ?? null,
    tdeeData: tdeeData ?? null,
    startDate,
    endDate,
    isLoading,
    error,
  };
};
