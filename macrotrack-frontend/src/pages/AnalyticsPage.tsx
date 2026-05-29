import { useState } from 'react';
import { useDashboard } from '../hooks/useDashboard';
import { useMultiScenarioProjection } from '../hooks/useMultiScenarioProjection';
import { useAnalyticsByPeriod } from '../hooks/useAnalyticsByPeriod';
import type { Period } from '../hooks/useAnalyticsByPeriod';

import { WeightTrendSection } from './Analytics/WeightTrendSection';
import { TdeeEstimatedSection } from './Analytics/TdeeEstimatedSection';
import { ForecastSection } from './Analytics/ForecastSection';

const AnalyticsPage = () => {
  const [weightPeriod, setWeightPeriod] = useState<Period>('1M');
  const [tdeePeriod, setTdeePeriod] = useState<Period>('1M');

  const { data: dashboardData } = useDashboard();
  const goal = dashboardData?.activeGoal ?? null;
  const currentWeight = dashboardData?.latestWeight ?? null;

  const { data: forecastData } = useMultiScenarioProjection(!!goal && currentWeight != null);

  const {
    weightTrend, startDate: wStart, endDate: wEnd, isLoading: wLoading, error: wError,
  } = useAnalyticsByPeriod(weightPeriod);

  const {
    tdeeData: tdeeDataSep, startDate: tStart, endDate: tEnd, isLoading: tLoading, error: tError,
  } = useAnalyticsByPeriod(tdeePeriod);

  return (
    <main className="bg-white min-h-screen pb-32 border-t-0">
      <div className="max-w-[800px] mx-auto py-8 px-5">
        <h1 className="font-display text-[3.5rem] text-gray-dark mb-12">
          ANALYSES
        </h1>

        <WeightTrendSection
          weightTrend={weightTrend}
          weightPeriod={weightPeriod}
          setWeightPeriod={setWeightPeriod}
          wLoading={wLoading}
          wError={wError}
          wStart={wStart}
          wEnd={wEnd}
          goal={goal}
        />

        <TdeeEstimatedSection
          tdeeDataSep={tdeeDataSep}
          tdeePeriod={tdeePeriod}
          setTdeePeriod={setTdeePeriod}
          tLoading={tLoading}
          tError={tError}
          tStart={tStart}
          tEnd={tEnd}
        />

        <ForecastSection forecastData={forecastData} />
      </div>
    </main>
  );
};

export default AnalyticsPage;

