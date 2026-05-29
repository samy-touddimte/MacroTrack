import { useState, useEffect } from 'react';
import { format } from 'date-fns';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import WeekDatePicker from './WeekDatePicker';
import { addFoodLog } from '../../services/foodLogApi';
import { calculateMacroCalories } from '../../utils/nutritionUtils';

interface QuickAddFormProps {
  selectedDate: Date;
  onDateChange: (d: Date) => void;
  onBack: () => void;
  onClose: () => void;
  loggedDates?: string[];
}

const QuickAddForm = ({ selectedDate, onDateChange, onBack, onClose, loggedDates }: QuickAddFormProps) => {
  const queryClient = useQueryClient();
  const [calories, setCalories] = useState('');
  const [protein, setProtein] = useState('');
  const [fat, setFat] = useState('');
  const [carbs, setCarbs] = useState('');
  const [name, setName] = useState('');

  useEffect(() => {
    setCalories('');
    setProtein('');
    setFat('');
    setCarbs('');
    setName('');
  }, [selectedDate]);

  const numProtein = Number(protein) || 0;
  const numFat = Number(fat) || 0;
  const numCarbs = Number(carbs) || 0;
  const sumMacros = calculateMacroCalories(numProtein, numFat, numCarbs);

  const mutation = useMutation({
    mutationFn: () => {
      return addFoodLog({
        date: format(selectedDate, 'yyyy-MM-dd'),
        caloriesKcal: Number(calories) || 0,
        proteinG: protein ? Number(protein) : undefined,
        fatG: fat ? Number(fat) : undefined,
        carbsG: carbs ? Number(carbs) : undefined,
        foodName: name.trim() || undefined,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['calendar'] });
      onClose();
    }
  });

  const submitQuickCalories = (e: React.FormEvent) => {
    e.preventDefault();
    if (!calories) return;
    mutation.mutate();
  };

  return (
    <form onSubmit={submitQuickCalories} className="flex flex-col h-full">
      <div className="flex items-center mb-2">
        <button type="button" onClick={onBack} className="text-2xl text-gray-400 mr-4">←</button>
        <h2 className="font-display text-xl tracking-wide flex-1 text-center pr-8">AJOUT RAPIDE</h2>
      </div>

      <WeekDatePicker selectedDate={selectedDate} onChange={onDateChange} loggedDates={loggedDates} />

      <div className="bg-[#F4F4F4] rounded-[16px] p-4 mb-4 flex flex-col items-center">
        <div className="flex items-baseline gap-2">
          <input
            type="number"
            value={calories}
            onChange={(e) => setCalories(e.target.value)}
            className="bg-transparent text-center font-display text-4xl w-[120px] p-0 text-black placeholder:text-gray-300"
            placeholder="0"
            min="0"
            step="1"
            required
            autoFocus
          />
          <span className="text-text-muted font-bold">kcal</span>
        </div>
        <span className="text-xs text-text-muted mt-1">
          Somme macros = {sumMacros} kcal
        </span>
      </div>

      <div className="grid grid-cols-3 gap-3 mb-4">
        {[
          { label: 'PROTÉINES', value: protein, setter: setProtein },
          { label: 'LIPIDES', value: fat, setter: setFat },
          { label: 'GLUCIDES', value: carbs, setter: setCarbs }
        ].map((macro) => (
          <div key={macro.label} className="bg-[#F4F4F4] rounded-[12px] p-3 flex flex-col">
            <span className="text-[0.65rem] text-text-muted font-bold tracking-wider mb-1">{macro.label}</span>
            <div className="flex items-baseline">
              <input
                type="number"
                value={macro.value}
                onChange={(e) => macro.setter(e.target.value)}
                className="bg-transparent w-full font-bold text-lg p-0 text-black placeholder:text-gray-300"
                placeholder="0"
                min="0"
                step="0.1"
              />
              <span className="text-text-muted text-sm ml-1">g</span>
            </div>
          </div>
        ))}
      </div>

      <div className="bg-[#F4F4F4] rounded-[12px] p-3 mb-6">
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="bg-transparent w-full p-0 text-black placeholder:text-text-muted text-sm"
          placeholder="Nom du repas (optionnel)"
        />
      </div>

      <button 
        type="submit" 
        disabled={!calories || mutation.isPending}
        className="w-full bg-[var(--color-purple)] text-white font-display text-lg py-4 rounded-xl tracking-wide disabled:opacity-50"
      >
        {mutation.isPending ? 'AJOUT...' : 'AJOUTER'}
      </button>
    </form>
  );
};

export default QuickAddForm;
