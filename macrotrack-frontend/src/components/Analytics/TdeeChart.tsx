import { ComposedChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { getVisibleTicks, formatXTick } from '../../utils/chartUtils';
import type { Period } from '../../hooks/useAnalyticsByPeriod';

const PURPLE = 'var(--color-purple)';
const TEXT_MUTED = 'var(--color-text-muted)';

interface TdeeChartProps {
  data: Array<{ date: string; tdee: number }>;
  period: Period;
  yDomain: [number | 'auto', number | 'auto'];
}

export const TdeeChart = ({ data, period, yDomain }: TdeeChartProps) => {
  if (data.length === 0) {
    return (
      <div className="h-[220px] flex items-center justify-center">
        <p className="text-text-muted text-[0.85rem]">Pas encore assez de données pour cette période.</p>
      </div>
    );
  }

  return (
    <ResponsiveContainer width="100%" height={220}>
      <ComposedChart data={data} margin={{ top: 8, right: 30, bottom: 0, left: -10 }}>
        <defs>
          <linearGradient id="tdeeGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor={PURPLE} stopOpacity={0.25} />
            <stop offset="100%" stopColor={PURPLE} stopOpacity={0} />
          </linearGradient>
        </defs>
        <XAxis
          dataKey="date"
          tick={{ fill: TEXT_MUTED, fontSize: 11 }}
          tickLine={false}
          axisLine={false}
          tickMargin={8}
          ticks={getVisibleTicks(data, period)}
          tickFormatter={(d) => formatXTick(d, period)}
        />
        <YAxis
          domain={yDomain}
          tick={{ fill: TEXT_MUTED, fontSize: 11 }}
          tickLine={false}
          axisLine={false}
          tickFormatter={(v) => `${v}`}
          tickMargin={8}
          width={60}
        />
        <Tooltip
          contentStyle={{ background: '#222', border: 'none', borderRadius: '8px', fontSize: '0.8rem' }}
          labelStyle={{ color: TEXT_MUTED, marginBottom: '4px' }}
          itemStyle={{ color: 'var(--color-white)' }}
          formatter={(val: number) => [`${Math.round(val)} kcal`, 'TDEE estimé']}
        />
        <Area
          type="monotone"
          dataKey="tdee"
          stroke={PURPLE}
          strokeWidth={2}
          fill="url(#tdeeGradient)"
          activeDot={{ r: 5, fill: PURPLE }}
          isAnimationActive={false}
        />
      </ComposedChart>
    </ResponsiveContainer>
  );
};
