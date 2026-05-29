import { useDashboard } from '../hooks/useDashboard';
import { useStartWeight } from '../hooks/useStartWeight';
import { calculateProgressPercentage } from '../utils/nutritionUtils';
import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { DailyMacroCard } from './Dashboard/DailyMacroCard';
import { TdeeCard } from './Dashboard/TdeeCard';
import { WeightProgressCard } from './Dashboard/WeightProgressCard';

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
      <section className="container section max-w-[800px] mx-auto py-8 px-4">
        <header className="mb-12">
          <p className="uppercase text-sm text-text-muted tracking-widest m-0 font-bold">
            {format(new Date(), 'EEEE, d MMMM', { locale: fr })}
          </p>
          <h1 className="text-[3.5rem] my-1">DASHBOARD</h1>
        </header>

        <DailyMacroCard data={data} />

        <div>
          <h2 className="text-[1.4rem] text-black tracking-wide mb-6">
            STATISTIQUES
          </h2>

          <div className="grid grid-cols-2 gap-6">
            <TdeeCard data={data} />
            <WeightProgressCard data={data} projection={projection} progressPercent={progressPercent} />
          </div>
        </div>
      </section>
    </main>
  );
}

export default DashboardPage;
