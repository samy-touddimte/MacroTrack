import { KCAL_PER_GRAM } from '../constants/nutrition';

export function calculateMacroCalories(protein: number = 0, fat: number = 0, carbs: number = 0): number {
  return (protein * KCAL_PER_GRAM.PROTEIN) + (fat * KCAL_PER_GRAM.FAT) + (carbs * KCAL_PER_GRAM.CARBS);
}

export function getTdeeDiffColor(diff: number | null): string {
  if (diff === null || diff === 0) return 'var(--color-text-muted)';
  return diff > 0 ? 'var(--color-green)' : 'var(--color-red)';
}

export function calculateProgressPercentage(start: number | null, current: number | null, target: number | null): number {
  if (start == null || current == null || target == null) return 0;
  if (target === start) return 100;

  if (target > start) {
    // prise de masse
    const totalToGain = target - start;
    return totalToGain > 0
      ? Math.max(0, Math.min(100, ((current - start) / totalToGain) * 100))
      : 0;
  } else {
    // perte de poids
    const totalToLose = start - target;
    return totalToLose > 0
      ? Math.max(0, Math.min(100, ((start - current) / totalToLose) * 100))
      : 0;
  }
}
