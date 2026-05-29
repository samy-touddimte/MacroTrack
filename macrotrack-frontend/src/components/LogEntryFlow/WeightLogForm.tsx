import { useState, useEffect } from 'react';
import { format } from 'date-fns';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import WeekDatePicker from './WeekDatePicker';
import { addWeightEntry, getWeightEntries } from '../../services/weightApi';

interface WeightLogFormProps {
  selectedDate: Date;
  onDateChange: (d: Date) => void;
  onBack: () => void;
  onClose: () => void;
  loggedDates?: string[];
}

const WeightLogForm = ({ selectedDate, onDateChange, onBack, onClose, loggedDates }: WeightLogFormProps) => {
  const queryClient = useQueryClient();
  const [weight, setWeight] = useState('');
  const [bodyFat, setBodyFat] = useState('');

  const dateStr = format(selectedDate, 'yyyy-MM-dd');
  
  const { data: weightEntries } = useQuery({
    queryKey: ['editDay-weight', dateStr],
    queryFn: () => getWeightEntries(dateStr, dateStr),
  });

  useEffect(() => {
    if (weightEntries && weightEntries.length > 0) {
      setWeight(weightEntries[0].weightKg.toString());
      setBodyFat(weightEntries[0].bodyFatPercentage ? weightEntries[0].bodyFatPercentage.toString() : '');
    } else {
      setWeight('');
      setBodyFat('');
    }
  }, [weightEntries]);

  const mutation = useMutation({
    mutationFn: () => {
      return addWeightEntry({
        date: format(selectedDate, 'yyyy-MM-dd'),
        weightKg: Number(weight),
        bodyFatPercentage: bodyFat ? Number(bodyFat) : undefined,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['calendar'] });
      queryClient.invalidateQueries({ queryKey: ['active-goal'] });
      onClose();
    }
  });

  const saveWeightEntry = (e: React.FormEvent) => {
    e.preventDefault();
    if (!weight) return;
    mutation.mutate();
  };

  return (
    <form onSubmit={saveWeightEntry} className="flex flex-col h-full">
      <div className="flex items-center mb-2">
        <button type="button" onClick={onBack} className="text-2xl text-gray-400 mr-4">←</button>
        <h2 className="font-display text-xl tracking-wide flex-1 text-center pr-8">POIDS DU JOUR</h2>
      </div>

      <WeekDatePicker selectedDate={selectedDate} onChange={onDateChange} loggedDates={loggedDates} />

      <div className="bg-[#F4F4F4] rounded-[16px] p-6 mb-4 flex flex-col items-center">
        <span className="text-[0.65rem] text-text-muted font-bold tracking-wider mb-2">POIDS ACTUEL</span>
        <div className="flex items-baseline gap-2">
          <input
            type="number"
            value={weight}
            onChange={(e) => setWeight(e.target.value)}
            className="bg-transparent text-center font-display text-5xl w-[140px] p-0 text-black placeholder:text-gray-300"
            placeholder="0.0"
            min="0"
            step="0.1"
            required
            autoFocus
          />
          <span className="text-text-muted font-bold text-xl">kg</span>
        </div>
      </div>

      <div className="bg-[#F4F4F4] rounded-[12px] p-4 mb-6">
        <div className="flex justify-between items-center">
          <span className="text-sm font-bold">Masse Grasse (%)</span>
          <input
            type="number"
            value={bodyFat}
            onChange={(e) => setBodyFat(e.target.value)}
            className="bg-transparent text-right font-bold w-[100px] p-0 text-black placeholder:text-text-muted text-sm"
            placeholder="Ex: 15.5"
            min="0"
            step="0.1"
          />
        </div>
      </div>

      <button 
        type="submit" 
        disabled={!weight || mutation.isPending}
        className="w-full bg-[var(--color-purple)] text-white font-display text-lg py-4 rounded-xl tracking-wide disabled:opacity-50 mt-auto"
      >
        {mutation.isPending ? 'ENREGISTREMENT...' : 'ENREGISTRER'}
      </button>
    </form>
  );
};

export default WeightLogForm;
