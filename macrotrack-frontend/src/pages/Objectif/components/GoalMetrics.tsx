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
    <div className="flex flex-row justify-between items-center p-4 md:p-5 rounded-2xl bg-white border border-gray-200 my-6 gap-2 text-center">
      <div className="flex-1 flex flex-col items-center">
        <span className="text-[0.55rem] md:text-[0.65rem] text-text-muted uppercase tracking-wide mb-1">POIDS ACTUEL</span>
        <span className="font-display text-xl md:text-2xl text-black">{currentWeight ?? '—'} <span className="text-sm">kg</span></span>
      </div>
      
      <div className="flex-1 flex flex-col items-center">
        <span className="text-[0.55rem] md:text-[0.65rem] text-text-muted uppercase tracking-wide mb-1">OBJECTIF</span>
        <span className="font-display text-xl md:text-2xl text-primary">{targetWeightKg ?? '—'} <span className="text-sm">kg</span></span>
      </div>

      <div className="flex-1 flex flex-col items-center">
        <span className="text-[0.55rem] md:text-[0.65rem] text-text-muted uppercase tracking-wide mb-1">RYTHME</span>
        <span className="font-display text-xl md:text-2xl text-black">
          {weeklyRateKg != null ? `${weeklyRateKg > 0 ? '+' : ''}${weeklyRateKg}` : '—'} <span className="text-xs">kg/s</span>
        </span>
      </div>
    </div>
  );
};

export default GoalMetrics;
