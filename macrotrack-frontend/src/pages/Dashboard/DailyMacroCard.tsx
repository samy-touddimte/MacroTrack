import React from 'react';
import DonutGauge from '../../components/DonutGauge/DonutGauge';
import type { DashboardData } from '../../types';

interface DailyMacroCardProps {
  data: DashboardData | null;
}

const renderMacroBar = (label: string, consumed: number, target: number, color: string) => (
  <div className="flex-1 text-center">
    <p className="text-[0.85rem] text-text-muted uppercase tracking-widest mb-2 font-bold">
      {label}
    </p>
    <div className="w-full h-[12px] bg-gray-light rounded-full overflow-hidden mb-2 mt-2">
      <div 
        className="h-full rounded-full transition-all duration-500 ease-out"
        style={{ width: `${Math.min(100, (consumed / (target || 1)) * 100)}%`, background: color }} 
      />
    </div>
    <p className="font-display text-[1.25rem] m-0">
      {Math.round(consumed)} / {Math.round(target)}g
    </p>
  </div>
);

export const DailyMacroCard: React.FC<DailyMacroCardProps> = ({ data }) => {
  return (
    <div className="mb-16 xl:pl-10">
      <h2 className="text-xl md:text-[1.4rem] text-black tracking-wide mb-4 md:mb-10 text-center uppercase">
        OBJECTIF JOURNALIER
      </h2>

      <DonutGauge value={data?.todayCaloriesKcal || 0} target={data?.dailyCalorieTarget || 2000} size={440} strokeWidth={36} />

      <div className="flex gap-8 mt-16">
        {renderMacroBar('PROTÉINES', data?.todayMacros?.proteinG || 0, data?.macroTargets?.proteinG || 0, '#FF6B6B')}
        {renderMacroBar('LIPIDES', data?.todayMacros?.fatG || 0, data?.macroTargets?.fatG || 0, '#FFD93D')}
        {renderMacroBar('GLUCIDES', data?.todayMacros?.carbsG || 0, data?.macroTargets?.carbsG || 0, '#6BCB77')}
      </div>
    </div>
  );
};
