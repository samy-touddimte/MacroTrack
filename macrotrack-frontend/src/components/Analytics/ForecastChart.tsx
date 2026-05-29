import { ComposedChart, Line, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { getForecastTicks, formatXTick } from '../../utils/chartUtils';

const PURPLE = 'var(--color-purple)';
const TEXT_MUTED = 'var(--color-text-muted)';

interface ForecastChartProps {
  data: Array<{ timestamp?: number; date: string; ideal?: number | null; empirical?: number | null }>;
  yDomain: [number | 'auto', number | 'auto'];
  hasEnoughDataForEmpirical: boolean;
}

export const ForecastChart = ({ data, yDomain, hasEnoughDataForEmpirical }: ForecastChartProps) => {
  if (data.length === 0) {
    return (
      <div className="h-[220px] flex flex-col items-center justify-center">
        <p className="text-text-muted text-[0.85rem]">Données insuffisantes pour générer la prévision.</p>
      </div>
    );
  }

  return (
    <div className="h-[250px] w-full">
      <ResponsiveContainer width="100%" height="100%">
        <ComposedChart data={data} margin={{ top: 8, right: 30, bottom: 0, left: -10 }}>
          <defs>
            <linearGradient id="forecastGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={PURPLE} stopOpacity={0.25} />
              <stop offset="100%" stopColor={PURPLE} stopOpacity={0} />
            </linearGradient>
          </defs>
          <XAxis
            dataKey="timestamp"
            type="number"
            domain={['dataMin', 'dataMax']}
            tick={{ fill: TEXT_MUTED, fontSize: 11 }}
            tickLine={false}
            axisLine={false}
            tickMargin={8}
            ticks={getForecastTicks(data).map(d => new Date(d).getTime())}
            tickFormatter={(t) => formatXTick(new Date(t).toISOString().split('T')[0], '1M')}
          />
          <YAxis
            domain={yDomain}
            tick={{ fill: TEXT_MUTED, fontSize: 11 }}
            tickLine={false}
            axisLine={false}
            tickFormatter={(v) => `${v}kg`}
            tickMargin={8}
            width={60}
          />
          <Tooltip
            contentStyle={{ background: '#222', border: 'none', borderRadius: '8px', fontSize: '0.8rem' }}
            labelStyle={{ color: TEXT_MUTED, marginBottom: '4px' }}
            itemStyle={{ color: 'var(--color-white)' }}
            formatter={(val: string | number | (string | number)[], name: string) => {
              if (val === undefined || val === null || isNaN(Number(val))) return [];
              return [
                `${Number(val).toFixed(1)} kg`, 
                name === 'ideal' ? 'Idéal' : 'Empirique'
              ] as [string, string];
            }}
          />
          <Area
            type="monotone"
            dataKey="ideal"
            stroke={PURPLE}
            strokeWidth={2}
            fill="url(#forecastGradient)"
            dot={false}
            activeDot={{ r: 5, fill: PURPLE }}
            isAnimationActive={false}
            connectNulls={true}
          />
          {hasEnoughDataForEmpirical && (
            <Line
              type="monotone"
              dataKey="empirical"
              stroke="#555555"
              strokeDasharray="5 5"
              strokeWidth={2}
              dot={false}
              activeDot={{ r: 4 }}
              isAnimationActive={false}
              connectNulls={true}
            />
          )}
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  );
};
