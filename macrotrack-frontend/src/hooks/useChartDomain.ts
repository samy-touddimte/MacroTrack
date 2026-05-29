import { useMemo } from 'react';

interface ChartDataPoint {
  date: string;
  value: number | null;
  [key: string]: unknown;
}

export function useChartDomain(data: ChartDataPoint[] | undefined, valueKey: string = 'value', minPadding: number = 2, maxPadding: number = 2) {
  const domainAndStats = useMemo(() => {
    if (!data || data.length === 0) return { yDomain: ['auto', 'auto'] as [number | 'auto', number | 'auto'], avg: 0, diff: 0, first: 0, last: 0 };

    const validValues = data
      .map(d => d[valueKey])
      .filter((v): v is number => typeof v === 'number');

    if (validValues.length === 0) return { yDomain: ['auto', 'auto'] as [number | 'auto', number | 'auto'], avg: 0, diff: 0, first: 0, last: 0 };

    const min = Math.min(...validValues);
    const max = Math.max(...validValues);
    const avg = validValues.reduce((sum, v) => sum + v, 0) / validValues.length;
    const first = validValues[0];
    const last = validValues[validValues.length - 1];
    const diff = last - first;

    return {
      yDomain: [Math.floor(min) - minPadding, Math.ceil(max) + maxPadding] as [number, number],
      avg,
      diff,
      first,
      last
    };
  }, [data, valueKey, minPadding, maxPadding]);

  return domainAndStats;
}
