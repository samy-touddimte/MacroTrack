import React, { useMemo } from 'react';
import { ChartHeader } from '../../components/Analytics/ChartHeader';
import { WeightTrendChart } from '../../components/Analytics/WeightTrendChart';
import { useChartDomain } from '../../hooks/useChartDomain';
import { PeriodSelector } from '../../components/Analytics/PeriodSelector';
import type { Period } from '../../hooks/useAnalyticsByPeriod';
import type { Goal, WeightTrendData } from '../../types';

interface WeightTrendSectionProps {
  weightTrend: WeightTrendData | null;
  weightPeriod: Period;
  setWeightPeriod: (p: Period) => void;
  wLoading: boolean;
  wError: string | null;
  wStart: string;
  wEnd: string;
  goal: Goal | null;
}

export const WeightTrendSection: React.FC<WeightTrendSectionProps> = ({
  weightTrend, weightPeriod, setWeightPeriod, wLoading, wError, wStart, wEnd, goal
}) => {
  const data = useMemo(() => weightTrend?.dynamicTrend ?? [], [weightTrend]);
  const { yDomain, avg, diff } = useChartDomain(data, 'value', 0.08, 0.05);

  const weightChartData = useMemo(() => {
    return data.map((p) => ({
      date: p.date,
      trend: parseFloat(p.value.toFixed(2)),
      raw: undefined,
    }));
  }, [data]);

  return (
    <section className="mb-14">
      <h2 className="font-display text-[1.2rem] text-text-muted tracking-widest mb-6">
        TENDANCE DU POIDS
      </h2>
      <div className="bg-gray-light rounded-2xl p-6 relative">
        <ChartHeader
          label="poids"
          moyenne={avg}
          difference={diff}
          unit="kg"
          startDate={wStart}
          endDate={wEnd}
          invertColor
          goalRate={goal?.weeklyRateKg}
        />

        {wLoading ? (
          <div className="h-[220px] flex items-center justify-center">
            <p className="text-text-muted">Chargement...</p>
          </div>
        ) : wError ? (
          <p className="text-[0.85rem]" style={{ color: 'var(--color-red)' }}>{wError}</p>
        ) : (
          <WeightTrendChart data={weightChartData} period={weightPeriod} yDomain={yDomain} />
        )}

        <PeriodSelector value={weightPeriod} onChange={setWeightPeriod} />
      </div>
    </section>
  );
};
