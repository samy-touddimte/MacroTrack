import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import type { ActiveForm } from './LogEntryFlow';

interface ActionSheetProps {
  selectedDate: Date;
  onSelectAction: (action: ActiveForm) => void;
}

const ActionSheet = ({ selectedDate, onSelectAction }: ActionSheetProps) => {
  const dateStr = format(selectedDate, 'EEEE d MMMM', { locale: fr }).toUpperCase();

  const tiles = [
    {
      id: 'quickAdd' as ActiveForm,
      title: 'AJOUT RAPIDE',
      bg: 'var(--color-gray-light)',
      color: 'var(--color-black)',
      border: 'none',
      isAvailable: true,
    },
    {
      id: 'weightLog' as ActiveForm,
      title: 'POIDS DU JOUR',
      bg: 'var(--color-gray-light)',
      color: 'var(--color-black)',
      border: 'none',
      isAvailable: true,
    },
    {
      id: 'editDay' as ActiveForm,
      title: 'MODIFIER LA JOURNÉE',
      bg: 'var(--color-gray-light)',
      color: 'var(--color-black)',
      border: 'none',
      isAvailable: true,
    },
  ];

  return (
    <div className="flex flex-col w-full">
      <div className="mb-6 flex flex-col items-center">
        <h2 className="font-display text-xl tracking-widest uppercase">{dateStr}</h2>
      </div>
      
      <div className="grid grid-cols-2 gap-4">
        {tiles.map((tile) => (
          <button
            key={tile.title}
            onClick={tile.isAvailable && tile.id ? () => onSelectAction(tile.id) : undefined}
            disabled={!tile.isAvailable}
            style={{
              background: tile.bg,
              color: tile.color,
              border: tile.border,
              borderRadius: 'var(--radius-lg, 16px)',
              cursor: tile.isAvailable ? 'pointer' : 'not-allowed',
              opacity: tile.isAvailable ? 1 : 0.7,
              transition: 'transform 150ms ease, box-shadow 150ms ease',
              position: 'relative',
              overflow: 'hidden',
            }}
            className="w-full flex flex-col items-center justify-center py-6 px-4 hover:scale-[1.01] active:scale-[0.98] shadow-sm gap-2"
          >
            <span
              className="font-display font-bold text-[1.1rem] tracking-wider uppercase text-center"
              style={{ color: tile.color }}
            >
              {tile.title}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
};

export default ActionSheet;
