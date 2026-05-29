import { format } from 'date-fns';
import { fr } from 'date-fns/locale';
import type { UseFormRegister, UseFormHandleSubmit, FieldErrors, UseFormWatch } from 'react-hook-form';
import type { UseMutationResult } from '@tanstack/react-query';
import type { Goal, ObjectifForm } from '../../../types';

interface GoalModalProps {
  isOpen: boolean;
  onClose: () => void;
  goal: Goal | null | undefined;
  estimatedDateFromApi: string | null;
  register: UseFormRegister<ObjectifForm>;
  handleSubmit: UseFormHandleSubmit<ObjectifForm>;
  errors: FieldErrors<ObjectifForm>;
  onSubmit: (values: ObjectifForm) => void;
  weeklyRateKg: number;
  sliderLabel: string;
  isCoherent: boolean;
  incoherentErrorMessage: string | null;
  sliderColor: string;
  modalCaloriesEstimate: number | null;
  extremeDeficit: boolean;
  updateMutation: UseMutationResult<Goal, Error, ObjectifForm, unknown>;
  defaultDate: string;
  watch: UseFormWatch<ObjectifForm>;
  currentWeight: number | null | undefined;
}

const GoalModal = ({
  isOpen,
  onClose,
  goal,
  estimatedDateFromApi,
  register,
  handleSubmit,
  onSubmit,
  weeklyRateKg,
  sliderLabel,
  isCoherent,
  incoherentErrorMessage,
  sliderColor,
  modalCaloriesEstimate,
  extremeDeficit,
  updateMutation,
  defaultDate,
  watch,
  currentWeight
}: GoalModalProps) => {
  if (!isOpen) return null;

  const targetWeightForm = watch('targetWeightKg');
  const isProjectionTooFar = isCoherent && !estimatedDateFromApi && currentWeight != null && weeklyRateKg !== 0 && (Math.abs(targetWeightForm - currentWeight) / Math.abs(weeklyRateKg) > 104);

  return (
    <div
      onClick={onClose}
      className="fixed inset-0 bg-black/50 backdrop-blur-md flex items-center justify-center z-[60] p-5"
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-[480px] bg-white rounded-2xl p-[30px] max-h-[90vh] overflow-y-auto text-black shadow-[0_20px_40px_rgba(0,0,0,0.15)] flex flex-col gap-[20px]"
      >
        <div className="flex justify-between items-center">
          <h2 className="font-display text-[2rem] text-black m-0 tracking-wide">
            {goal ? "MODIFIER L'OBJECTIF" : "CRÉER UN OBJECTIF"}
          </h2>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-5">
          <div>
            <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
              POIDS CIBLE
            </label>
            <div className="custom-input-container">
              <input
                type="number"
                step="0.1"
                {...register('targetWeightKg', { required: true, valueAsNumber: true })}
                className="flex-1 border-none bg-transparent text-center text-black text-xl font-medium outline-none p-0 font-body"
              />
              <span className="text-text-muted text-base font-medium ml-2 font-body">kg</span>
            </div>
          </div>

          <div>
            <div className="flex justify-between items-baseline mb-2">
              <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider">
                VITESSE
              </label>
              <span className="font-body text-[11px] font-medium tracking-wide uppercase" style={{ color: sliderColor }}>
                {sliderLabel}
              </span>
            </div>
            <div>
              <input
                type="range"
                min={-1}
                max={1}
                step={0.1}
                {...register('weeklyRateKg', { valueAsNumber: true })}
                className="custom-range-slider"
                style={{
                  background: `linear-gradient(90deg, var(--color-purple) 0%, var(--color-purple) ${(weeklyRateKg + 1) * 50}%, #E0E0E0 ${(weeklyRateKg + 1) * 50}%, #E0E0E0 100%)`
                }}
              />
              <div className="flex justify-between text-[11px] text-text-muted mt-2 font-medium px-[2px] font-body">
                <span className={"font-medium " + (weeklyRateKg === -1 ? "text-black" : "text-inherit")}>−1</span>
                <span className={"font-medium " + (weeklyRateKg === -0.5 ? "text-black" : "text-inherit")}>−0.5</span>
                <span className={"font-medium " + (weeklyRateKg === 0 ? "text-black" : "text-inherit")}>maintien</span>
                <span className={"font-medium " + (weeklyRateKg === 0.5 ? "text-black" : "text-inherit")}>+0.5</span>
                <span className={"font-medium " + (weeklyRateKg === 1 ? "text-black" : "text-inherit")}>+1</span>
              </div>
            </div>
          </div>

          {incoherentErrorMessage && (
            <div className="bg-[#FEF2F2] border-l-[3px] rounded-md p-2.5 font-body text-xs leading-[1.4] text-left" style={{ borderColor: 'var(--color-red)', color: 'var(--color-red)' }}>
              {incoherentErrorMessage}
            </div>
          )}

          {extremeDeficit && !incoherentErrorMessage && isCoherent && (
            <div className="bg-[#FFF7ED] border-l-[3px] rounded-md p-2.5 flex items-start gap-2" style={{ borderColor: 'var(--color-orange)' }}>
              <span className="text-sm mt-0.5" style={{ color: 'var(--color-orange)' }}>⚠️</span>
              <span className="font-body text-xs leading-[1.4] text-left" style={{ color: 'var(--color-orange)' }}>
                <strong>Attention :</strong> Ce rythme nécessite un déficit calorique extrême (&lt; 1000 kcal). Il est recommandé de réduire la vitesse de perte ou d'augmenter votre dépense énergétique.
              </span>
            </div>
          )}

          <div className="flex gap-3">
            <div className="flex-1 bg-gray-light rounded-xl px-4 py-3 text-center flex flex-col gap-1">
              <div className="font-body text-[1.15rem] font-medium text-black leading-tight">
                {isCoherent && modalCaloriesEstimate !== null ? `${modalCaloriesEstimate} kcal` : '—'}
              </div>
              <div className="text-[9px] text-text-muted uppercase tracking-wider font-medium font-body">
                budget calorique cible
              </div>
            </div>
            <div className="flex-1 bg-gray-light rounded-xl px-4 py-3 text-center flex flex-col gap-1">
              <div className="font-body text-[1.15rem] font-medium text-black leading-tight">
                {(() => {
                  if (isCoherent && estimatedDateFromApi) return format(new Date(estimatedDateFromApi), 'd MMMM yyyy', { locale: fr });
                  if (isProjectionTooFar) return "> 2 ans";
                  return '—';
                })()}
              </div>
              <div className="text-[9px] text-text-muted uppercase tracking-wider font-medium font-body">
                {isProjectionTooFar ? "projection trop lointaine" : "date d'atteinte estimée"}
              </div>
            </div>
          </div>

          <div>
            <label className="font-body text-[0.8rem] font-medium text-black uppercase tracking-wider block mb-2">
              DATE DE DÉBUT
            </label>
            <div className="custom-input-container">
              <input
                type="date"
                {...register('startDate')}
                defaultValue={goal?.startDate || defaultDate}
                className="w-full border-none bg-transparent text-black text-base outline-none p-0 font-body"
              />
            </div>
          </div>

          <div className="flex gap-3 mt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-[0.8rem] px-4 rounded-full border-none bg-gray-light hover:bg-[#EEEEEE] text-black font-body font-medium text-[0.85rem] tracking-wide cursor-pointer transition-all duration-150 ease-in-out"
            >
              ANNULER
            </button>
            <button
              type="submit"
              disabled={!isCoherent || updateMutation.isPending}
              className={"flex-1 py-[0.8rem] px-4 rounded-full border-none bg-primary text-white font-body font-medium text-[0.85rem] tracking-wide transition-all duration-150 ease-in-out " + (!isCoherent ? "cursor-not-allowed opacity-40 pointer-events-none" : "cursor-pointer")}
            >
              {updateMutation.isPending ? 'ENREGISTREMENT...' : 'ENREGISTRER'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default GoalModal;
