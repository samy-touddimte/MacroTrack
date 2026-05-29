import { useGoalData } from './useGoalData';
import { useGoalForm } from './useGoalForm';
import { useGoalMutation } from './useGoalMutation';

export const useGoalManager = () => {
  const goalData = useGoalData();
  const updateMutation = useGoalMutation(goalData.goal);
  
  const goalForm = useGoalForm({
    goal: goalData.goal,
    currentTdee: goalData.currentTdee,
    startWeight: goalData.startWeight,
    currentWeight: goalData.currentWeight,
    updateMutation,
  });

  return {
    ...goalData,
    ...goalForm,
    updateMutation,
  };
};
