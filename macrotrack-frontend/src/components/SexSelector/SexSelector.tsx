interface SexSelectorProps {
  value: string;
  onChange: (value: string) => void;
}

export const SexSelector = ({ value, onChange }: SexSelectorProps) => {
  return (
    <div className={`flex gap-3 h-[50px]`}>
      {[{ label: 'Homme', value: 'MALE' }, { label: 'Femme', value: 'FEMALE' }].map((opt) => {
        const active = value === opt.value;
        return (
          <button
            key={opt.value}
            type="button"
            onClick={() => onChange(opt.value)}
            className={`selectable-card-center ${active ? 'active' : ''}`}
          >
            {opt.label}
          </button>
        );
      })}
    </div>
  );
};
