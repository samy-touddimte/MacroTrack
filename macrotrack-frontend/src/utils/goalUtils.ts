import { Goal, AdherenceMetrics } from '../types';
import { startOfDay, differenceInDays } from 'date-fns';

export function calculateWeightLossStats(
  goal: Goal | null,
  startWeight: number | null,
  currentWeight: number | null
) {
  let weightAlreadyLost = 0;
  let weightRemaining = 0;

  if (goal) {
    const startingWeight = startWeight ?? currentWeight ?? goal.targetWeightKg;
    const target = goal.targetWeightKg;
    const totalDelta = Math.abs(startingWeight - target);
    
    const isLossGoal = target < startingWeight;
    const currentVal = currentWeight ?? startingWeight;
    
    const isTrendingCorrectly = isLossGoal
      ? currentVal <= startingWeight
      : currentVal >= startingWeight;

    if (isTrendingCorrectly) {
      weightAlreadyLost = Math.round(Math.abs(startingWeight - currentVal) * 10) / 10;
    }
    
    weightRemaining = Math.round(Math.max(0, totalDelta - weightAlreadyLost) * 10) / 10;
  }

  return { weightAlreadyLost, weightRemaining };
}

export function calculateAdherenceScore(adherence: AdherenceMetrics | undefined): number {
  if (adherence && !adherence.insufficientData) {
    return Math.round((adherence.loggedAdherence ?? 0) * 5);
  }
  return -1;
}
