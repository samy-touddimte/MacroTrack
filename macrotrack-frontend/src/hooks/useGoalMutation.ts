import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateGoal, createGoal } from '../services/goalApi';
import type { ObjectifForm, Goal } from '../types';

export const useGoalMutation = (goal: Goal | null) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (values: ObjectifForm) => {
      const payload = {
        targetWeightKg: values.targetWeightKg,
        weeklyRateKg: values.weeklyRateKg,
        startDate: values.startDate,
      };
      return goal ? updateGoal(goal.id, payload) : createGoal(payload);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['active-goal'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['multi-scenario-projection'] });
      queryClient.invalidateQueries({ queryKey: ['projection'] });
    }
  });
};
