import { useEffect, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { UseMutationResult } from '@tanstack/react-query';
import { fetchMetabolicForecast } from '../services/analyticsApi';
import { getLocalDateString } from '../utils/dateUtils';
import type { ObjectifForm, Goal } from '../types';

interface UseGoalFormProps {
  goal?: Goal | null;
  currentTdee?: number | null;
  startWeight?: number | null;
  currentWeight?: number | null;
  updateMutation: UseMutationResult<Goal, Error, ObjectifForm, unknown>;
}

export const useGoalForm = ({ goal, currentTdee, startWeight, currentWeight, updateMutation }: UseGoalFormProps) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [estimatedDateFromApi, setEstimatedDateFromApi] = useState<string | null>(null);
  const [extremeDeficit, setExtremeDeficit] = useState<boolean>(false);
  const defaultDate = getLocalDateString();

  const form = useForm<ObjectifForm>({
    defaultValues: {
      currentWeightKg: 0,
      targetWeightKg: 0,
      startDate: defaultDate,
      weeklyRateKg: -0.5,
    },
  });

  const { register, handleSubmit, watch, setValue, formState: { errors } } = form;

  const rawWeeklyRateKg = watch('weeklyRateKg', -0.5);
  const weeklyRateKg = Math.round(rawWeeklyRateKg * 10) / 10;
  const targetWeightForm = watch('targetWeightKg', 0);
  const startDateForm = watch('startDate', defaultDate);

  useEffect(() => {
    if (goal) {
      setValue('targetWeightKg', goal.targetWeightKg);
      setValue('weeklyRateKg', goal.weeklyRateKg);
      setValue('startDate', goal.startDate || defaultDate);
    }
  }, [goal, setValue, defaultDate, estimatedDateFromApi]);

  useEffect(() => {
    if (!isModalOpen || targetWeightForm == null || !startDateForm) return;
    const weightForCalculation = currentWeight ?? startWeight ?? null;
    if (weightForCalculation == null) return;
    const numericRate = Number(weeklyRateKg);
    const numericTargetWeight = Number(targetWeightForm);
    if (numericRate === 0 || isNaN(numericRate) || isNaN(numericTargetWeight) || numericTargetWeight <= 0) {
      setEstimatedDateFromApi(null);
      return;
    }

    let cancelled = false;
    const fetchDate = async () => {
      try {
        const result = await fetchMetabolicForecast({
          currentWeight: weightForCalculation,
          goalWeight: Number(targetWeightForm),
          weeklyRate: numericRate,
          startDate: startDateForm,
        });
        if (!cancelled) {
          setEstimatedDateFromApi(result.estimatedReachDate ?? null);
          setExtremeDeficit(result.extremeDeficit ?? false);
        }
      } catch (err) {
        console.error('Échec de la récupération des prévisions métaboliques pour la modal :', err);
        if (!cancelled) {
          setEstimatedDateFromApi(null);
          setExtremeDeficit(false);
        }
      }
    };

    fetchDate();
    return () => { cancelled = true; };
  }, [isModalOpen, currentWeight, startWeight, targetWeightForm, weeklyRateKg, startDateForm]);

  const sliderLabel = useMemo(() => {
    if (weeklyRateKg === 0) return 'maintien du poids';
    if (weeklyRateKg < 0) return `${weeklyRateKg.toFixed(2)} kg/semaine`;
    return `+${weeklyRateKg.toFixed(2)} kg/semaine`;
  }, [weeklyRateKg]);

  const isCoherent = useMemo(() => {
    if (currentWeight === null || currentWeight === undefined) return true;

    const targetWeight = targetWeightForm;
    const isMaintien = weeklyRateKg === 0;
    const isPerte = weeklyRateKg < 0;
    const isPrise = weeklyRateKg > 0;

    return (
      (isPerte && targetWeight < currentWeight - 1) ||
      (isPrise && targetWeight > currentWeight + 1) ||
      (isMaintien && Math.abs(targetWeight - currentWeight) <= 1)
    );
  }, [currentWeight, targetWeightForm, weeklyRateKg]);

  const incoherentErrorMessage = useMemo(() => {
    if (isCoherent || currentWeight === null || currentWeight === undefined) return null;

    const isMaintien = weeklyRateKg === 0;
    const isPerte = weeklyRateKg < 0;
    const isPrise = weeklyRateKg > 0;

    if (isMaintien && Math.abs(targetWeightForm - currentWeight) > 1) {
      return "en mode maintien, le poids cible doit être proche de ton poids actuel (±1 kg).";
    }
    if (isPrise && targetWeightForm < currentWeight) {
      return "vitesse de prise incompatible avec un poids cible inférieur au tien.";
    }
    if (isPerte && targetWeightForm > currentWeight) {
      return "vitesse de perte incompatible avec un poids cible supérieur au tien.";
    }
    return null;
  }, [isCoherent, currentWeight, weeklyRateKg, targetWeightForm]);

  const sliderColor = useMemo(() => {
    if (weeklyRateKg === 0) return 'var(--color-text-muted)';
    return 'var(--color-purple)';
  }, [weeklyRateKg]);

  const modalCaloriesEstimate = useMemo(() => {
    if (currentTdee == null) return null;
    return Math.round(currentTdee + weeklyRateKg * 1100);
  }, [currentTdee, weeklyRateKg]);

  const openModal = () => {
    if (goal) {
      setValue('targetWeightKg', goal.targetWeightKg);
      setValue('weeklyRateKg', goal.weeklyRateKg);
      setValue('startDate', goal.startDate || defaultDate);
    }
    setIsModalOpen(true);
  };

  const closeModal = () => setIsModalOpen(false);

  const onSubmit = (values: ObjectifForm) => {
    updateMutation.mutate(values, {
      onSuccess: () => {
        setIsModalOpen(false);
      }
    });
  };

  return {
    isModalOpen,
    openModal,
    closeModal,
    estimatedDateFromApi,
    form,
    register,
    handleSubmit,
    errors,
    weeklyRateKg,
    sliderLabel,
    isCoherent,
    incoherentErrorMessage,
    sliderColor,
    modalCaloriesEstimate,
    onSubmit,
    extremeDeficit,
  };
};
