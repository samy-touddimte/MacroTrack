import React from 'react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { UseMutationResult } from '@tanstack/react-query';

import type { Goal } from '../../../types';

interface GoalHistoryProps {
  history: Goal[];
  deleteMutation: UseMutationResult<void, Error, number, unknown>;
}

const GoalHistory: React.FC<GoalHistoryProps> = ({ history, deleteMutation }) => {
  if (history.length === 0) return null;

  return (
    <div className="mt-10">
      <h3 className="font-body text-xs text-text-muted uppercase tracking-wide mb-4">
        HISTORIQUE DES OBJECTIFS
      </h3>
      <div className="flex flex-col gap-3">
        {history.map((hGoal) => {
          const rateSign = hGoal.weeklyRateKg > 0 ? '+' : '';
          const rateLabel = `${rateSign}${hGoal.weeklyRateKg} kg/sem`;
          const startDateLabel = format(new Date(hGoal.startDate), 'd MMMM yyyy', { locale: fr });
          
          return (
            <div key={hGoal.id} className="card bg-[#F4F4F4] rounded-2xl p-4 flex flex-col gap-2">
              <div className="flex justify-between items-start">
                <span className="font-body text-[11px] text-text-muted">
                  Début : {startDateLabel} · {rateLabel}
                </span>
                <button
                  onClick={() => deleteMutation.mutate(hGoal.id)}
                  className="text-text-muted hover:text-[color:var(--color-red)] transition-colors text-xl leading-none font-bold bg-transparent border-none cursor-pointer p-0"
                  aria-label="Supprimer l'objectif"
                >
                  ×
                </button>
              </div>
              <div>
                <span className="font-body text-lg font-bold text-black">
                  {hGoal.targetWeightKg} kg
                </span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default GoalHistory;
