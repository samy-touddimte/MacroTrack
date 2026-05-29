import { useQuery } from '@tanstack/react-query';
import { getWeightEntries } from '../services/weightApi';
import { getLocalDateString } from '../utils/dateUtils';
import type { DashboardData } from '../types';

export const useStartWeight = (dashboardData: DashboardData | null) => {
  const goalStartDate = dashboardData?.activeGoal?.startDate;
  const latestWeight = dashboardData?.latestWeight;
  
  return useQuery({
    queryKey: ['startWeight', goalStartDate],
    queryFn: async () => {
      if (!goalStartDate || latestWeight == null) {
        return { startWeight: null, progressPercent: 0 };
      }
      
      const today = getLocalDateString();
      const entries = await getWeightEntries(goalStartDate, today);
      const sorted = entries.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
      
      const start = sorted.length > 0 ? sorted[0].weightKg : (latestWeight ?? 0);
      return { startWeight: start };
    },
    enabled: !!goalStartDate && latestWeight != null,
    staleTime: 5 * 60 * 1000,
  });
};
