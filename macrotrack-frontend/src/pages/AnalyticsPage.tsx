import { useState } from 'react';
import { useDashboard } from '../hooks/useDashboard';
import { useMultiScenarioProjection } from '../hooks/useMultiScenarioProjection';
import { useAnalyticsByPeriod } from '../hooks/useAnalyticsByPeriod';
import type { Period } from '../hooks/useAnalyticsByPeriod';

import { WeightTrendSection } from './Analytics/WeightTrendSection';
import { TdeeEstimatedSection } from './Analytics/TdeeEstimatedSection';
import { ForecastSection } from './Analytics/ForecastSection';
import PageTitle from '../components/PageTitle/PageTitle';

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
      <div className="max-w-[800px] xl:max-w-[1200px] mx-auto py-8 px-5">
        <PageTitle title="ANALYSES" />

        <div className="grid grid-cols-1 xl:grid-cols-2 gap-8 xl:gap-12 mt-6 mb-8">
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

          <ForecastSection forecastData={forecastData} />
        </div>

        <div>
          <TdeeEstimatedSection
            tdeeDataSep={tdeeDataSep}
            tdeePeriod={tdeePeriod}
            setTdeePeriod={setTdeePeriod}
            tLoading={tLoading}
            tError={tError}
            tStart={tStart}
            tEnd={tEnd}
          />
        </div>
      </div>
    </main>
  );
};

export default AnalyticsPage;

