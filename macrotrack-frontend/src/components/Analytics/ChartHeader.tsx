import { format, parseISO, isValid } from 'date-fns';
import { fr } from 'date-fns/locale';
import { getTdeeDiffColor } from '../../utils/nutritionUtils';

const TEXT_MUTED = 'var(--color-text-muted)';

interface ChartHeaderProps {
  label: string;
  moyenne: number | null;
  difference: number | null;
  unit: string;
  startDate: string;
  endDate: string;
  invertColor?: boolean;
  goalRate?: number | null;
}

export const ChartHeader = ({ label, moyenne, difference, unit, startDate, endDate, invertColor, goalRate }: ChartHeaderProps) => {
  const diffColor = () => {
    if (difference === null || difference === 0) return TEXT_MUTED;
    if (invertColor) {
      const isGaining = difference > 0;
      const wantsToGain = goalRate !== undefined && goalRate !== null && goalRate > 0;
      const wantsToLose = goalRate !== undefined && goalRate !== null && goalRate < 0;
      
      if (wantsToGain) return isGaining ? 'var(--color-purple)' : 'var(--color-red)';
      if (wantsToLose) return isGaining ? 'var(--color-red)' : 'var(--color-green)';
      
      return isGaining ? 'var(--color-purple)' : '#4ADE80';
    }
    return getTdeeDiffColor(difference);
  };

  const formatDate = (d: string) => {
    const parsed = parseISO(d);
    return isValid(parsed) ? format(parsed, 'd MMM yyyy', { locale: fr }) : d;
  };

  return (
    <div className="flex justify-between items-start mb-4">
      <div>
        <p className="text-[0.65rem] text-text-muted uppercase tracking-widest m-0 mb-0.5 font-bold">
          Moyenne
        </p>
        <p className="font-display text-[1.4rem] sm:text-[2rem] text-gray-dark m-0 leading-none">
          {moyenne !== null ? `${moyenne.toFixed(1)} ${unit}` : '-'}
        </p>
      </div>
      <div className="text-right flex flex-col items-end">
        <p className="text-[0.65rem] text-text-muted uppercase tracking-widest m-0 mb-0.5 font-bold">
          Différence
        </p>
        <div className="flex items-center gap-3">
          <p className="font-display text-[1.4rem] sm:text-[2rem] m-0 leading-none" style={{ color: diffColor() }}>
            {difference !== null
              ? `${difference > 0 ? '+' : ''}${difference.toFixed(1)} ${unit}`
              : '-'}
          </p>
        </div>
      </div>
      <div className="absolute inset-x-0 text-center pointer-events-none">
        <p className="text-[0.65rem] text-text-muted m-0">
          {formatDate(startDate)} – {formatDate(endDate)}
        </p>
      </div>
    </div>
  );
};
