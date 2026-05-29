import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import PageTitle from '../../components/PageTitle/PageTitle';
import { useGoalManager } from '../../hooks/useGoalManager';
import { getLocalDateString } from '../../utils/dateUtils';
import { getGoalHistory, deleteGoal } from '../../services/goalApi';

import WeeklyStrategyBar from './components/WeeklyStrategyBar';
import GoalMetrics from './components/GoalMetrics';
import GoalActions from './components/GoalActions';
import GoalProgress from './components/GoalProgress';
import DietAdherence from './components/DietAdherence';
import GoalHistory from './components/GoalHistory';
import GoalModal from './components/GoalModal';

const ObjectifPage = () => {
  const queryClient = useQueryClient();
  const { data: history = [] } = useQuery({
    queryKey: ['goalHistory'],
    queryFn: getGoalHistory,
  });

  const deleteMutation = useMutation({
    mutationFn: deleteGoal,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['goalHistory'] });
      queryClient.invalidateQueries({ queryKey: ['active-goal'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    }
  });

  const {
    goal,
    startWeight,
    currentWeight,
    dailyCalorieTarget,
    error,
    isModalOpen,
    openModal,
    closeModal,
    estimatedDateFromApi,
    form,
    register,
    handleSubmit,
    errors,
    weeklyRateKg,
    sliderLabel,
    isCoherent,
    incoherentErrorMessage,
    sliderColor,
    modalCaloriesEstimate,
    updateMutation,
    onSubmit,
    weightAlreadyLost,
    weightRemaining,
    score,
    scoreColor,
    extremeDeficit,
  } = useGoalManager();

  const defaultDate = getLocalDateString();
  const targetWeightForm = form.watch('targetWeightKg');

  return (
    <main className="bg-white min-h-screen">
      <section className="container section max-w-[800px] mx-auto py-8 px-5 text-black">
        <div className="mb-6">
          <PageTitle title="MON OBJECTIF" />
        </div>

        {error && <div className="form-error">{error}</div>}

        <WeeklyStrategyBar dailyCalorieTarget={dailyCalorieTarget} />
        
        <GoalMetrics 
          currentWeight={currentWeight}
          targetWeightKg={goal?.targetWeightKg ?? null}
          weeklyRateKg={goal?.weeklyRateKg ?? null}
        />

        <GoalActions 
          goalExists={!!goal}
          onOpenModal={openModal}
        />

        <GoalProgress 
          weightRemaining={weightRemaining}
          weightAlreadyLost={weightAlreadyLost}
          dailyCalorieTarget={dailyCalorieTarget}
          estimatedDateFromApi={estimatedDateFromApi}
        />

        <DietAdherence 
          score={score}
          scoreColor={scoreColor}
        />

        <GoalHistory 
          history={history}
          deleteMutation={deleteMutation}
        />

        <GoalModal
          isOpen={isModalOpen}
          onClose={closeModal}
          goal={goal}
          estimatedDateFromApi={estimatedDateFromApi}
          register={register}
          handleSubmit={handleSubmit}
          errors={errors}
          onSubmit={onSubmit}
          weeklyRateKg={weeklyRateKg}
          sliderLabel={sliderLabel}
          isCoherent={isCoherent}
          incoherentErrorMessage={incoherentErrorMessage}
          sliderColor={sliderColor}
          modalCaloriesEstimate={modalCaloriesEstimate}
          extremeDeficit={extremeDeficit}
          updateMutation={updateMutation}
          defaultDate={defaultDate}
          watch={form.watch}
          currentWeight={currentWeight}
        />
      </section>
    </main>
  );
};

export default ObjectifPage;

