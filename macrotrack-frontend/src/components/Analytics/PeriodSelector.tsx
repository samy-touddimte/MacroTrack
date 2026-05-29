import type { Period } from '../../hooks/useAnalyticsByPeriod';

const PERIODS: Period[] = ['1S', '1M', '3M', '6M', '1A', 'TOUT'];

interface PeriodSelectorProps {
  value: Period;
  onChange: (p: Period) => void;
}

export const PeriodSelector = ({ value, onChange }: PeriodSelectorProps) => (
  <div className="flex gap-1.5 justify-center mt-4">
    {PERIODS.map((p) => {
      const active = p === value;
      return (
        <button
          key={p}
          onClick={() => onChange(p)}
          className={"px-3 py-1.5 rounded-full border text-[0.75rem] font-display tracking-wide cursor-pointer " + (active ? "border-primary bg-primary text-gray-dark" : "border-[#333333] bg-transparent text-text-muted")}
        >
          {p}
        </button>
      );
    })}
  </div>
);
