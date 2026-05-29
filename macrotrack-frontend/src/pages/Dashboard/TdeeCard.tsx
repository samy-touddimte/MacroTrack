import React from 'react';
import type { DashboardData } from '../../types';

interface TdeeCardProps {
  data: DashboardData | null;
}

export const TdeeCard: React.FC<TdeeCardProps> = ({ data }) => {
  const confidence = Math.min(data?.confidence || 1, 5);
  const currentTdee = data?.currentTdee ?? 0;

  return (
    <div className="card">
      <div className="mb-3">
        <p className="text-xs font-bold m-0 leading-tight">TDEE</p>
        <p className="text-xs font-bold m-0 leading-tight text-text-muted">ESTIMÉ</p>
      </div>

      <p className="font-display text-4xl md:text-[2.8rem] text-primary m-0 leading-none">
        {Math.round(currentTdee)} KCAL
      </p>

      <div className="mt-6">
        <div className="flex gap-1">
          {[1, 2, 3, 4, 5].map(i => (
            <div key={i} className={"flex-1 h-[6px] rounded-[3px] " + (i <= confidence ? "bg-primary" : "bg-white")} />
          ))}
        </div>
        <p className="text-xs text-text-muted font-semibold mt-2.5 mb-0">
          CONFIANCE {confidence}/5
        </p>
      </div>

      {data?.adherence && !data.adherence.insufficientData && (
        <div className="mt-6 border-t border-[#E5E5E5] pt-4">
          <p className="text-[0.7rem] text-text-muted font-bold mb-3 tracking-widest uppercase">
            Adhérence au plan
          </p>
          <div className="flex justify-between gap-3">
            {[
              { label: '7j', value: Math.round((data.adherence.adherence7d ?? 0) * 100) },
              { label: '14j', value: Math.round((data.adherence.adherence14d ?? 0) * 100) },
              { label: '30j', value: Math.round((data.adherence.adherence30d ?? 0) * 100) },
            ].map(metric => (
              <div key={metric.label} className="flex-1">
                <div className="w-full h-[4px] bg-white rounded-[2px] overflow-hidden mb-1.5">
                  <div 
                    className="h-full bg-black transition-all duration-500 ease-in-out rounded-[2px]" 
                    style={{ width: `${metric.value}%` }} 
                  />
                </div>
                <p className="text-[0.65rem] font-bold text-text-muted text-center m-0">
                  {metric.value}% <span className="text-[0.55rem] font-normal uppercase">{metric.label}</span>
                </p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
