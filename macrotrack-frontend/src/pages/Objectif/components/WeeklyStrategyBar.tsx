import React from 'react';

interface WeeklyStrategyBarProps {
  dailyCalorieTarget: number | null | undefined;
}

const DAYS = [
  { label: 'L', id: 1 },
  { label: 'M', id: 2 },
  { label: 'M', id: 3 },
  { label: 'J', id: 4 },
  { label: 'V', id: 5 },
  { label: 'S', id: 6 },
  { label: 'D', id: 0 }
];

const WeeklyStrategyBar: React.FC<WeeklyStrategyBarProps> = ({ dailyCalorieTarget }) => {
  const currentDayId = new Date().getDay();

  return (
    <div className="flex gap-1 sm:gap-2 overflow-x-auto pb-2 scrollbar-hide justify-start">
      {DAYS.map((day, index) => {
        const isToday = day.id === currentDayId;
        const bgColor = isToday ? 'bg-[var(--color-purple)] text-white' : 'bg-transparent text-black';
        const labelColor = isToday ? 'text-white/80' : 'text-text-muted';

        return (
          <div 
            key={index} 
            className={`flex flex-col items-center justify-center shrink-0 w-[46px] sm:w-[50px] h-[56px] rounded-[16px] ${bgColor}`}
          >
            <span className={`text-xs ${labelColor}`}>{day.label}</span>
            <span className="font-bold text-[0.85rem] leading-tight mt-0.5">
              {dailyCalorieTarget ? Math.round(dailyCalorieTarget) : '—'}
            </span>
          </div>
        );
      })}
    </div>
  );
};

export default WeeklyStrategyBar;
