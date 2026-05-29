import React, { useMemo } from 'react';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { ForecastChart } from '../../components/Analytics/ForecastChart';
import { interpolateForecastData } from '../../utils/chartUtils';
import type { MultiScenarioProjectionResponse } from '../../types';

interface ForecastSectionProps {
  forecastData: MultiScenarioProjectionResponse | undefined;
}

export const ForecastSection: React.FC<ForecastSectionProps> = ({ forecastData }) => {
  const empiricalIsTooFar = useMemo(() => {
    if (!forecastData || !forecastData.idealDate) return false;
    if (!forecastData.empiricalDate) return true;
    const idealDate = new Date(forecastData.idealDate);
    const empDate = new Date(forecastData.empiricalDate);
    const diffTime = empDate.getTime() - idealDate.getTime();
    const diffDays = diffTime / (1000 * 3600 * 24);
    return diffDays > 30;
  }, [forecastData]);

  const forecastChartData = useMemo(() => {
    if (!forecastData) return [];
    const ideal = forecastData.idealPoints || [];
    const empirical = forecastData.empiricalPoints || [];
    const map = new Map();
    ideal.forEach(p => map.set(p.date, { date: p.date, ideal: p.value }));
    if (ideal.length === 0) {
      empirical.forEach(p => map.set(p.date, { date: p.date, empirical: p.value }));
    } else {
      if (empiricalIsTooFar) {
        const maxIdealDate = ideal[ideal.length - 1].date;
        let passedMax = false;
        empirical.forEach(p => {
          if (!passedMax) {
            const existing = map.get(p.date) || { date: p.date };
            map.set(p.date, { ...existing, empirical: p.value });
            if (p.date >= maxIdealDate) {
              passedMax = true;
            }
          }
        });
      } else {
        empirical.forEach(p => {
          const existing = map.get(p.date) || { date: p.date };
          map.set(p.date, { ...existing, empirical: p.value });
        });
      }
    }
    const sorted = Array.from(map.values()).sort((a, b) => a.date.localeCompare(b.date));
    interpolateForecastData(sorted, 'ideal');
    interpolateForecastData(sorted, 'empirical');
    return sorted.map(d => ({
      ...d,
      timestamp: new Date(d.date).getTime()
    }));
  }, [forecastData, empiricalIsTooFar]);

  const forecastYDomain = useMemo((): [number, number] => {
    if (forecastChartData.length === 0) return [60, 100];
    const vals = forecastChartData.flatMap(p => [p.ideal, p.empirical]).filter(v => v !== undefined && v !== null && !isNaN(v));
    if (vals.length === 0) return [60, 100];
    const dataMin = Math.min(...vals);
    const dataMax = Math.max(...vals);
    const range = (dataMax - dataMin) || 10;
    return [Math.floor(dataMin - range * 0.08), Math.ceil(dataMax + range * 0.08)];
  }, [forecastChartData]);

  const forecastMeta = useMemo(() => {
    if (!forecastData) return { dateAtteinte: null, depasseDeuxAns: false };
    const twoYearsDays = 730;
    const depasse = (forecastData.idealPoints?.length ?? 0) >= twoYearsDays;
    const date = depasse ? null : forecastData.idealDate ?? null;
    return {
      dateAtteinte: date,
      depasseDeuxAns: depasse,
    };
  }, [forecastData]);

  if (!forecastData || !forecastData.idealPoints || forecastData.idealPoints.length === 0) {
    return null;
  }

  return (
    <section className="mt-14">
      <h2 className="font-display text-base md:text-[1.2rem] text-text-muted tracking-widest mb-4 md:mb-6">
        PRÉVISIONS
      </h2>
      <div className="bg-gray-light rounded-2xl p-6 relative">
        {!forecastData.hasEnoughDataForEmpirical && (
          <p className="text-[0.75rem] text-text-muted italic m-0 mb-6 opacity-80">
            * Votre trajectoire personnalisée s'affichera après 5 jours d'historique.
          </p>
        )}

        <div className="flex justify-between items-start mb-4">
          <div>
            <p className="text-[0.65rem] text-text-muted uppercase tracking-widest m-0 mb-0.5 font-bold">
              Date estimée (Idéale)
            </p>
            <p className="font-display text-2xl md:text-[2rem] text-gray-dark m-0 leading-none">
              {(() => {
                if (forecastMeta.depasseDeuxAns) return 'plus de 2 ans';
                if (forecastMeta.dateAtteinte) return format(new Date(forecastMeta.dateAtteinte), 'd MMMM yyyy', { locale: fr });
                return '—';
              })()}
            </p>
          </div>
          {forecastData.hasEnoughDataForEmpirical && (
            <div className="text-right">
              <p className="text-[0.65rem] text-text-muted uppercase tracking-widest m-0 mb-0.5 font-bold">
                Date estimée (Empirique)
              </p>
              <p className="font-display text-2xl md:text-[2rem] text-gray-dark m-0 leading-none">
                {(() => {
                  if (forecastMeta.depasseDeuxAns) return 'plus de 2 ans';
                  if (forecastData.empiricalDate) return format(new Date(forecastData.empiricalDate), 'd MMMM yyyy', { locale: fr });
                  return '—';
                })()}
              </p>
            </div>
          )}
        </div>

        <ForecastChart data={forecastChartData} yDomain={forecastYDomain} hasEnoughDataForEmpirical={forecastData.hasEnoughDataForEmpirical || false} />

        {forecastMeta.depasseDeuxAns && (
          <p className="m-0 text-[0.7rem] text-text-muted italic">
            * projection limitée à 2 ans maximum
          </p>
        )}
      </div>
    </section>
  );
};
