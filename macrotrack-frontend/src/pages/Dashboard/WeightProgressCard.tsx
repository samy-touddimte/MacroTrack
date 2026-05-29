import React from 'react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import type { DashboardData, ProjectionData } from '../../types';

interface WeightProgressCardProps {
  data: DashboardData | null;
  projection: ProjectionData | null;
  progressPercent: number;
}

export const WeightProgressCard: React.FC<WeightProgressCardProps> = ({ data, projection, progressPercent }) => {
  return (
    <div className="card relative">
      <div className="flex justify-between items-end">
        <div>
          <div className="mb-3">
            <p className="text-xs font-bold m-0 leading-tight">POIDS</p>
            <p className="text-xs font-bold m-0 leading-tight text-text-muted">ACTUEL</p>
          </div>
          <p className="font-display text-[2.8rem] m-0 leading-none">
            {data?.latestWeight} <span className="text-[1.2rem]">KG</span>
          </p>
        </div>

        <div className="text-right">
          <div className="mb-3">
            <p className="text-xs font-bold m-0 leading-tight">POIDS</p>
            <p className="text-xs font-bold m-0 leading-tight text-primary">CIBLE</p>
          </div>
          <p className="font-display text-[2.8rem] m-0 leading-none text-primary">
            {data?.activeGoal?.targetWeightKg} <span className="text-[1.2rem]">KG</span>
          </p>
        </div>
      </div>

      <div className="mt-6">
        <div className="w-full h-[6px] bg-white rounded-[3px] overflow-hidden">
          <div className="h-full bg-primary" style={{ width: `${progressPercent}%` }} />
        </div>
        <div className="flex justify-between mt-2.5">
          <span className="font-display text-base text-primary">
            {progressPercent.toFixed(1)}% ATTEINT
          </span>
          {projection?.targetReachedDate ? (
            <span className="text-xs text-text-muted font-semibold">
              {format(new Date(projection.targetReachedDate), 'd MMM yyyy', { locale: fr })}
            </span>
          ) : (
            <span className="text-xs text-text-muted font-semibold">
              Inconnu
            </span>
          )}
        </div>

        {projection?.extremeDeficit && (
          <div className="bg-[#FFF7ED] border-l-[3px] rounded-md p-2.5 mt-4 flex items-start gap-2" style={{ borderColor: 'var(--color-orange)' }}>
            <span className="text-sm mt-0.5" style={{ color: 'var(--color-orange)' }}>⚠️</span>
            <span className="text-[#EA580C] font-body text-xs leading-[1.4] text-left">
              <strong>Attention :</strong> Ce rythme nécessite un déficit calorique extrême (&lt; 1000 kcal). Il est recommandé de réduire la vitesse de perte ou d'augmenter votre dépense énergétique.
            </span>
          </div>
        )}
      </div>
    </div>
  );
};
