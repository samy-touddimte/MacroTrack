import { format, parseISO, isValid } from 'date-fns';
import { fr } from 'date-fns/locale';
import type { Period } from '../hooks/useAnalyticsByPeriod';

export const formatXTick = (dateStr: string, period: Period) => {
  const d = parseISO(dateStr);
  if (!isValid(d)) return dateStr;
  switch (period) {
    case '1S':
    case '1M':
      return format(d, 'd MMM', { locale: fr });
    case '3M':
    case '6M':
    case '1A':
    case 'TOUT':
      return format(d, 'MMM yyyy', { locale: fr });
    default:
      return format(d, 'd MMM', { locale: fr });
  }
};

const tickInterval = (period: Period): number => {
  switch (period) {
    case '1S': return 1;
    case '1M': return 3;
    case '3M': return 14;
    case '6M': return 30;
    case '1A': return 60;
    case 'TOUT': return 90;
    default: return 30;
  }
};

export const getVisibleTicks = (data: Array<{ date: string }>, period: Period): string[] => {
  if (data.length === 0) return [];
  const firstDate = data[0].date;
  const lastDate = data[data.length - 1].date;
  if (period === '1S') return data.map(d => d.date);
  const step = tickInterval(period);
  const ticks: string[] = [firstDate];
  for (let i = step; i < data.length - 1; i += step) {
    ticks.push(data[i].date);
  }
  if (ticks[ticks.length - 1] !== lastDate) {
    ticks.push(lastDate);
  }
  return ticks;
};

export const getForecastTicks = (data: Array<{ date: string }>): string[] => {
  if (data.length === 0) return [];
  const firstDate = data[0].date;
  const lastDate = data[data.length - 1].date;
  if (data.length <= 12) return data.map(d => d.date);
  const step = Math.max(1, Math.floor(data.length / 12));
  const ticks: string[] = [firstDate];
  for (let i = step; i < data.length - 1; i += step) {
    ticks.push(data[i].date);
  }
  if (ticks[ticks.length - 1] !== lastDate) {
    ticks.push(lastDate);
  }
  return ticks;
};

export const interpolateForecastData = <T extends { date: string }>(arr: T[], key: keyof T) => {
  let lastValidIdx = -1;
  for (let i = 0; i < arr.length; i++) {
    if (arr[i][key] !== undefined && arr[i][key] !== null) {
      if (lastValidIdx !== -1 && i - lastValidIdx > 1) {
        const startVal = Number(arr[lastValidIdx][key]);
        const endVal = Number(arr[i][key]);
        const startDate = new Date(arr[lastValidIdx].date).getTime();
        const endDate = new Date(arr[i].date).getTime();
        for (let j = lastValidIdx + 1; j < i; j++) {
          const curDate = new Date(arr[j].date).getTime();
          const ratio = (curDate - startDate) / (endDate - startDate);
          const interpolatedValue = parseFloat((startVal + ratio * (endVal - startVal)).toFixed(2));
          // We assume the type T allows number assignment to the specific key
          (arr[j] as Record<keyof T, unknown>)[key] = interpolatedValue;
        }
      }
      lastValidIdx = i;
    }
  }
};
