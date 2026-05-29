import React from 'react';

interface GoalMetricsProps {
  currentWeight: number | null | undefined;
  targetWeightKg: number | null | undefined;
  weeklyRateKg: number | null | undefined;
}

const GoalMetrics: React.FC<GoalMetricsProps> = ({
  currentWeight,
  targetWeightKg,
  weeklyRateKg
}) => {
  return (
    <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center p-5 rounded-2xl bg-white border border-gray-200 my-6 gap-4 sm:gap-0">
      <div className="flex-1 flex flex-col">
        <span className="text-[0.65rem] text-text-muted uppercase tracking-wide mb-1">POIDS ACTUEL</span>
        <span className="font-display text-2xl text-black">{currentWeight ?? '—'} kg</span>
      </div>
      
      
      
      <div className="flex-1 flex flex-col">
        <span className="text-[0.65rem] text-text-muted uppercase tracking-wide mb-1">OBJECTIF</span>
        <span className="font-display text-2xl text-primary">{targetWeightKg ?? '—'} kg</span>
      </div>



      <div className="flex-1 flex flex-col">
        <span className="text-[0.65rem] text-text-muted uppercase tracking-wide mb-1">RYTHME</span>
        <span className="font-display text-2xl text-black">
          {weeklyRateKg != null ? `${weeklyRateKg > 0 ? '+' : ''}${weeklyRateKg} kg/sem` : '—'}
        </span>
      </div>
    </div>
  );
};

export default GoalMetrics;
