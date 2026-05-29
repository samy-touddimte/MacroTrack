import { useEffect, useRef } from 'react';
import { addDays, format, isSameDay, subDays } from 'date-fns';
import { fr } from 'date-fns/locale';

interface WeekDatePickerProps {
  selectedDate: Date;
  onChange: (date: Date) => void;
  loggedDates?: string[]; // ISO string dates (yyyy-MM-dd)
}

const WeekDatePicker = ({ selectedDate, onChange, loggedDates = [] }: WeekDatePickerProps) => {
  const containerRef = useRef<HTMLDivElement>(null);
  
  const days = Array.from({ length: 7 }).map((_, i) => subDays(addDays(selectedDate, i), 3));

  useEffect(() => {
    if (containerRef.current) {
      const activeItem = containerRef.current.querySelector('.active-day');
      if (activeItem) {
        activeItem.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
      }
    }
  }, [selectedDate]);

  return (
    <div 
      ref={containerRef}
      className="flex gap-2 overflow-x-auto snap-x snap-mandatory scrollbar-hide py-2 -mx-6 px-6 mb-6"
    >
      {days.map((date) => {
        const isActive = isSameDay(date, selectedDate);
        const isLogged = loggedDates.includes(format(date, 'yyyy-MM-dd'));
        const dayLetter = format(date, 'EE', { locale: fr }).charAt(0).toUpperCase();
        const dayNumber = format(date, 'd');

        return (
          <div
            key={date.toISOString()}
            onClick={() => onChange(date)}
            className={`snap-center flex flex-col items-center justify-center shrink-0 w-[44px] h-[56px] rounded-[var(--radius-md)] cursor-pointer transition-colors ${
              isActive ? 'bg-[var(--color-purple)] text-white active-day' : 'bg-transparent text-black'
            }`}
          >
            <span className={`text-xs ${isActive ? 'text-white/80' : 'text-text-muted'}`}>
              {dayLetter}
            </span>
            <span className="font-bold text-lg leading-tight mt-0.5">{dayNumber}</span>
            
            <div className="h-[4px] mt-1">
              {isLogged && (
                <div className={`w-1 h-1 rounded-full ${isActive ? 'bg-white' : 'bg-[var(--color-purple)]'}`} />
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default WeekDatePicker;
