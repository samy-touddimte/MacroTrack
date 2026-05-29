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
    <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
      {DAYS.map((day, index) => {
        const isToday = day.id === currentDayId;
        const bgColor = isToday ? 'bg-primary text-white' : 'bg-[#F4F4F4] text-text-muted';
        const textColor = isToday ? 'text-white' : 'text-black';
        const labelColor = isToday ? 'text-white/80' : 'text-text-muted';

        return (
          <div 
            key={index} 
            className={`flex-1 min-w-[60px] flex flex-col items-center justify-center p-3 rounded-2xl ${bgColor}`}
          >
            <span className={`text-xs font-medium mb-1 ${labelColor}`}>{day.label}</span>
            <span className={`text-sm font-bold ${textColor}`}>
              {dailyCalorieTarget ? Math.round(dailyCalorieTarget) : '—'}
            </span>
          </div>
        );
      })}
    </div>
  );
};

export default WeeklyStrategyBar;
