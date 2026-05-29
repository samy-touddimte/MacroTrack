import React from 'react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';

interface GoalProgressProps {
  weightRemaining: number;
  weightAlreadyLost: number;
  dailyCalorieTarget: number | null | undefined;
  estimatedDateFromApi: string | null | undefined;
}

const GoalProgress: React.FC<GoalProgressProps> = ({
  weightRemaining,
  weightAlreadyLost,
  dailyCalorieTarget,
  estimatedDateFromApi
}) => {
  return (
    <div>
      <h3 className="font-body text-sm font-bold text-text-muted uppercase tracking-wide mb-4 invisible">
        CALORIES RESTANTES
      </h3>
      <div className="card bg-[#F4F4F4] rounded-2xl p-5 flex flex-col gap-4">
        <div className="flex justify-between items-center pb-4">
        <span className="font-body text-sm font-bold text-black">{weightRemaining} kg restants</span>
        <span className="font-body text-sm text-text-muted">{weightAlreadyLost} kg perdus</span>
      </div>
      
      <div className="flex justify-between items-center">
        <div className="flex flex-col">
          <span className="font-display text-lg text-black">
            {dailyCalorieTarget ? `${Math.round(dailyCalorieTarget)} kcal` : '—'}
          </span>
          <span className="text-[11px] text-text-muted uppercase tracking-wide">budget calorique cible</span>
        </div>
        
        <div className="flex flex-col text-right">
          <span className="font-display text-lg text-black">
            {estimatedDateFromApi
              ? format(new Date(estimatedDateFromApi), 'd MMMM yyyy', { locale: fr })
              : '—'}
          </span>
          <span className="text-[11px] text-text-muted uppercase tracking-wide">objectif estimé</span>
        </div>
      </div>
    </div>
    </div>
  );
};

export default GoalProgress;
