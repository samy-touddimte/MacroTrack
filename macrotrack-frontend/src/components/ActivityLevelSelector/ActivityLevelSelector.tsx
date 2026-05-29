interface ActivityLevelSelectorProps {
  value: string;
  onChange: (value: string) => void;
}

const ACTIVITY_OPTIONS = [
  { value: 'SEDENTARY', label: 'Sédentaire', desc: 'Activité faible (ex: travail de bureau)' },
  { value: 'MODERATELY_ACTIVE', label: 'Moyennement Actif', desc: 'Activité modérée (ex: sport 2-3x/semaine)' },
  { value: 'VERY_ACTIVE', label: 'Très Actif', desc: 'Activité intense (ex: entraînement quotidien)' }
] as const;

export const ActivityLevelSelector = ({ value, onChange }: ActivityLevelSelectorProps) => {
  return (
    <div className="grid grid-cols-1 gap-3 w-full">
      {ACTIVITY_OPTIONS.map((opt) => {
        const active = value === opt.value;
        return (
          <button
            key={opt.value}
            type="button"
            onClick={() => onChange(opt.value)}
            className={`selectable-card h-full ${active ? 'active' : ''}`}
          >
            <span className={`font-bold text-[0.95rem] font-body ${active ? 'text-primary' : 'text-black'}`}>
              {opt.label}
            </span>
            <span className="text-xs text-text-muted font-body">
              {opt.desc}
            </span>
          </button>
        );
      })}
    </div>
  );
};
