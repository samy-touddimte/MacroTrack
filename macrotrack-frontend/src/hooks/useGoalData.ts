import axios from 'axios';
import { useQuery } from '@tanstack/react-query';
import { getActiveGoal } from '../services/goalApi';
import { useDashboard } from './useDashboard';
import { useStartWeight } from './useStartWeight';
import { calculateProgressPercentage } from '../utils/nutritionUtils';
import { calculateWeightLossStats, calculateAdherenceScore } from '../utils/goalUtils';

export const useGoalData = () => {
  const { data: goal, isLoading: loadingGoal, error: queryError } = useQuery({
    queryKey: ['active-goal'],
    queryFn: async () => {
      try {
        return await getActiveGoal();
      } catch (error) {
        if (axios.isAxiosError(error) && error.response?.status === 404) {
          return null;
        }
        throw error;
      }
    },
    staleTime: 5 * 60 * 1000,
  });

  const { data: dashboard, isLoading: loadingDashboard } = useDashboard();
  const { data: startWeightData } = useStartWeight(dashboard);

  const loading = loadingGoal || loadingDashboard;
  const error = queryError ? "Impossible de charger l'objectif." : '';

  const startWeight = startWeightData?.startWeight ?? null;
  const currentWeight = dashboard?.latestWeight ?? null;
  
  let progressPercent = 0;
  if (startWeight != null && currentWeight != null && goal) {
    progressPercent = calculateProgressPercentage(startWeight, currentWeight, goal.targetWeightKg);
  }

  const { weightAlreadyLost, weightRemaining } = calculateWeightLossStats(goal ?? null, startWeight, currentWeight);
  const score = calculateAdherenceScore(dashboard?.adherence);

  return {
    goal: goal ?? null,
    currentTdee: dashboard?.currentTdee ?? null,
    startWeight,
    currentWeight,
    progressPercent,
    dailyCalorieTarget: dashboard?.dailyCalorieTarget ?? null,
    loading,
    error,
    weightAlreadyLost,
    weightRemaining,
    score,
    scoreColor: 'var(--color-purple)',
  };
};
