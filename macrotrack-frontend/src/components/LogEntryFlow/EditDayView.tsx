
import React from 'react';
import { format } from 'date-fns';
import { useDailyLogs } from '../../hooks/useDailyLogs';
import WeekDatePicker from './WeekDatePicker';
import type { FoodLog } from '../../types';

interface EditDayViewProps {
  selectedDate: Date;
  onDateChange: (d: Date) => void;
  onBack: () => void;
  loggedDates?: string[];
}

const EditDayView = ({ selectedDate, onDateChange, onBack, loggedDates }: EditDayViewProps) => {
  const dateStr = format(selectedDate, 'yyyy-MM-dd');
  const { foodLogs, isLoading, deleteFoodLog, isDeleting } = useDailyLogs(dateStr);

  const { totalCalories, totalProtein, totalFat, totalCarbs } = React.useMemo(() => ({
    totalCalories: foodLogs.reduce((s, f) => s + f.caloriesKcal, 0),
    totalProtein: foodLogs.reduce((s, f) => s + (f.proteinG || 0), 0),
    totalFat: foodLogs.reduce((s, f) => s + (f.fatG || 0), 0),
    totalCarbs: foodLogs.reduce((s, f) => s + (f.carbsG || 0), 0),
  }), [foodLogs]);




  return (
    <div className="flex flex-col h-full bg-white relative">
      {/* Header */}
      <header
        className="sticky top-0 bg-white z-10 px-6 pt-6 pb-4"
        style={{ borderBottom: '1px solid var(--color-gray-light)' }}
      >
        <div className="flex items-center mb-4">
          <button type="button" onClick={onBack} className="text-2xl text-gray-400 mr-4 border-none bg-transparent cursor-pointer">←</button>
          <h2 className="font-display text-xl tracking-wide flex-1 text-center pr-8 m-0 uppercase">MODIFIER LA JOURNÉE</h2>
        </div>

        <div className="mb-5 flex items-center justify-between">
          <div className="flex-1 min-w-0 pr-4 relative">
            {/* Wrapper allows internal scrolling without bleeding too far if we want it next to the weight */}
            <div className="-mr-6">
              <WeekDatePicker selectedDate={selectedDate} onChange={onDateChange} loggedDates={loggedDates} />
            </div>
          </div>
        </div>

        {/* Macro summary bars */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          {[
            { label: 'CALORIES', value: Math.round(totalCalories), unit: 'kcal', color: 'var(--color-purple)' },
            { label: 'PROTÉINES', value: Math.round(totalProtein), unit: 'g', color: 'var(--color-black)' },
            { label: 'LIPIDES', value: Math.round(totalFat), unit: 'g', color: 'var(--color-black)' },
            { label: 'GLUCIDES', value: Math.round(totalCarbs), unit: 'g', color: 'var(--color-black)' },
          ].map((macro) => (
            <div
              key={macro.label}
              className="flex flex-col items-center py-2 rounded-[var(--radius-md)]"
              style={{ background: 'var(--color-gray-light)' }}
            >
              <span className="text-[0.55rem] font-bold text-text-muted tracking-wider">{macro.label}</span>
              <span className="font-display text-lg leading-tight" style={{ color: macro.color }}>
                {macro.value}
              </span>
              <span className="text-[0.6rem] text-text-muted font-semibold">{macro.unit}</span>
            </div>
          ))}
        </div>
      </header>

      {/* Timeline body */}
      <div className="flex-1 px-6 py-6 max-w-[850px] mx-auto w-full">
        {isLoading ? (
          <div className="flex items-center justify-center py-16">
            <p className="text-text-muted font-display text-lg tracking-wide">CHARGEMENT...</p>
          </div>
        ) : foodLogs.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16 gap-3">
            <p className="text-text-muted font-display text-lg tracking-wide">AUCUN REPAS</p>
            <p className="text-text-muted text-sm text-center">
              Utilisez le bouton + pour ajouter un repas.
            </p>
          </div>
        ) : (
          <div className="flex flex-col gap-3 max-w-[450px]">
            {foodLogs.map((food) => {
              const id = food.id;
              const entryKey = `food-${id}`;
                return (
                  <div key={entryKey}>
                    <div
                      className="rounded-[var(--radius-md)] p-4 transition-all duration-150"
                      style={{ background: 'var(--color-gray-light)', border: 'none' }}
                    >
                      <div className="flex items-start justify-between">
                        <div className="flex-1 min-w-0">
                          <p className="font-bold text-sm truncate mb-1">
                            {food.foodName || 'Repas sans nom'}
                          </p>
                          <div className="flex items-center gap-2 flex-wrap">
                            <span className="text-xs font-semibold" style={{ color: 'var(--color-text-muted)' }}>
                              {Math.round(food.proteinG || 0)}P
                            </span>
                            <span className="text-[0.6rem] text-text-muted">•</span>
                            <span className="text-xs font-semibold" style={{ color: 'var(--color-text-muted)' }}>
                              {Math.round(food.carbsG || 0)}G
                            </span>
                            <span className="text-[0.6rem] text-text-muted">•</span>
                            <span className="text-xs font-semibold" style={{ color: 'var(--color-text-muted)' }}>
                              {Math.round(food.fatG || 0)}L
                            </span>
                          </div>
                        </div>
                        <div className="flex items-center gap-3 ml-3">
                          <span className="font-display text-lg whitespace-nowrap">
                            {Math.round(food.caloriesKcal)} KCAL
                          </span>
                          <button
                            type="button"
                            onClick={() => deleteFoodLog(id)}
                            disabled={isDeleting}
                            className="w-8 h-8 rounded-full bg-transparent border-none cursor-pointer flex items-center justify-center text-gray-300 hover:text-red-500 hover:bg-red-50 transition-colors"
                            aria-label="Supprimer"
                          >
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                              <polyline points="3 6 5 6 21 6"></polyline>
                              <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                            </svg>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default EditDayView;
