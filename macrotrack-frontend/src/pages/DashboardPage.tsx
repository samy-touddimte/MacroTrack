import { useDashboard } from '../hooks/useDashboard';
import { useStartWeight } from '../hooks/useStartWeight';
import { calculateProgressPercentage } from '../utils/nutritionUtils';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { DailyMacroCard } from './Dashboard/DailyMacroCard';
import { TdeeCard } from './Dashboard/TdeeCard';
import { WeightProgressCard } from './Dashboard/WeightProgressCard';
import PageTitle from '../components/PageTitle/PageTitle';

function DashboardPage() {
  const { data, projection, isLoading, error, refetchAll } = useDashboard();
  const { data: startWeightData } = useStartWeight(data);



  if (isLoading) {
    return (
      <main>
        <div className="container section">
          <div className="loading-spinner">
            <div className="spinner"></div>
            <p>Chargement du tableau de bord...</p>
          </div>
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main>
        <div className="container section">
          <div className="error-state">
            <h2>Erreur de chargement</h2>
            <p>{error}</p>
            <button className="button-pill primary" onClick={refetchAll}>
              RÉESSAYER
            </button>
          </div>
        </div>
      </main>
    );
  }

  const startWeight = startWeightData?.startWeight ?? data?.latestWeight ?? 0;
  const current = data?.latestWeight ?? 0;
  const target = data?.activeGoal?.targetWeightKg ?? current;
  const progressPercent = calculateProgressPercentage(startWeight, current, target);

  return (
    <main className="bg-white min-h-screen">
      <section className="w-full max-w-[800px] xl:max-w-none mx-auto py-8 px-4 xl:px-12">
        <PageTitle 
          title="DASHBOARD" 
          dateSubtitle={format(new Date(), 'EEEE, d MMMM', { locale: fr })} 
        />

        <div className="grid grid-cols-1 xl:grid-cols-3 gap-8 xl:gap-12 mt-6">
          <div className="xl:col-span-2">
            <DailyMacroCard data={data} />
          </div>

          <div className="xl:col-span-1">
            <h2 className="text-lg md:text-[1.4rem] text-black tracking-wide mb-4 md:mb-6">
              STATISTIQUES
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-1 gap-6">
              <TdeeCard data={data} />
              <WeightProgressCard data={data} projection={projection} progressPercent={progressPercent} />
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}

export default DashboardPage;
