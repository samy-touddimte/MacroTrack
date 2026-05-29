import React, { useMemo } from 'react';
import { ChartHeader } from '../../components/Analytics/ChartHeader';
import { TdeeChart } from '../../components/Analytics/TdeeChart';
import { useChartDomain } from '../../hooks/useChartDomain';
import { PeriodSelector } from '../../components/Analytics/PeriodSelector';
import type { Period } from '../../hooks/useAnalyticsByPeriod';
import type { TdeeData } from '../../types';

interface TdeeEstimatedSectionProps {
  tdeeDataSep: TdeeData | null;
  tdeePeriod: Period;
  setTdeePeriod: (p: Period) => void;
  tLoading: boolean;
  tError: string | null;
  tStart: string;
  tEnd: string;
}

export const TdeeEstimatedSection: React.FC<TdeeEstimatedSectionProps> = ({
  tdeeDataSep, tdeePeriod, setTdeePeriod, tLoading, tError, tStart, tEnd
}) => {
  const data = tdeeDataSep?.tdeeEstimated ?? [];
  const { yDomain, avg, diff } = useChartDomain(data, 'value', 50, 50);

  const tdeeChartData = useMemo(() => {
    return data.map((p) => ({
      date: p.date,
      tdee: Math.round(p.value),
    }));
  }, [data]);

  return (
    <section>
      <h2 className="font-display text-[1.2rem] text-text-muted tracking-widest mb-6">
        TDEE ESTIMÉ
      </h2>
      <div className="bg-gray-light rounded-2xl p-6 relative">
        <ChartHeader
          label="tdee"
          moyenne={avg}
          difference={diff}
          unit="kcal"
          startDate={tStart}
          endDate={tEnd}
        />

        {tLoading ? (
          <div className="h-[220px] flex items-center justify-center">
            <p className="text-text-muted">Chargement...</p>
          </div>
        ) : tError ? (
          <p className="text-[0.85rem]" style={{ color: 'var(--color-red)' }}>{tError}</p>
        ) : (
          <TdeeChart data={tdeeChartData} period={tdeePeriod} yDomain={yDomain} />
        )}

        <PeriodSelector value={tdeePeriod} onChange={setTdeePeriod} />
      </div>
    </section>
  );
};
