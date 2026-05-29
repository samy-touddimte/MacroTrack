import { useForm } from 'react-hook-form';
import { useMemo } from 'react';
import { Link } from 'react-router-dom';
import { SexSelector } from '../../../components/SexSelector/SexSelector';
import { ActivityLevelSelector } from '../../../components/ActivityLevelSelector/ActivityLevelSelector';
import type { RegisterRequest } from '../../../types';
import { SLIDER_VALUES } from '../../../constants/sliderValues';

interface RegisterFormProps {
  onSubmit: (data: RegisterRequest) => void;
  apiError: string;
}

export const RegisterForm = ({ onSubmit, apiError }: RegisterFormProps) => {
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<RegisterRequest>({
    defaultValues: {
      weeklyRateKg: -0.5,
      sex: 'MALE',
      activityLevel: 'SEDENTARY',
    },
  });

  const weeklyRateKg = watch('weeklyRateKg', -0.5);
  const sexValue = watch('sex', 'MALE');
  const activityLevelValue = watch('activityLevel', 'SEDENTARY');

  const activeIndex = useMemo(() => {
    const idx = SLIDER_VALUES.indexOf(Number(weeklyRateKg));
    return idx >= 0 ? idx : 5; // fallback to index of -0.5
  }, [weeklyRateKg]);

  return (
    <form
      className="flex flex-col gap-10"
      onSubmit={handleSubmit(onSubmit)}
    >
      {apiError && <div className="form-error text-center">{apiError}</div>}

      {/* Section 1 : Votre Compte */}
      <div className="flex flex-col gap-6">
        <h3 className="font-display text-2xl tracking-wide pb-2 mb-2">
          1. Votre Compte
        </h3>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 gap-y-6">
          <div className="input-group mb-0 lg:col-span-2">
            <span className="input-label">Email</span>
            <input
              type="email"
              className="input-field"
              placeholder="vous@example.com"
              {...register('email', {
                required: 'Email requis',
                pattern: {
                  value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                  message: 'Email invalide',
                },
              })}
            />
            {errors.email && <span className="input-error">{errors.email.message}</span>}
          </div>

          <div className="input-group mb-0">
            <span className="input-label">Nom d'utilisateur</span>
            <input
              type="text"
              className="input-field"
              placeholder="Choisissez un pseudo"
              {...register('username', {
                required: 'Nom d\'utilisateur requis',
                minLength: {
                  value: 3,
                  message: 'Minimum 3 caractères',
                },
              })}
            />
            {errors.username && <span className="input-error">{errors.username.message}</span>}
          </div>

          <div className="input-group mb-0">
            <span className="input-label">Mot de passe</span>
            <input
              type="password"
              className="input-field"
              placeholder="Créez un mot de passe"
              {...register('password', {
                required: 'Mot de passe requis',
                minLength: { value: 8, message: 'Minimum 8 caractères' },
                pattern: {
                  value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!_.\-]).{8,}$/,
                  message: 'Doit contenir >=8 caractères, majuscule, minuscule, chiffre et caractère spécial (@#$%^&+=!_.-)'
                },
              })}
            />
            {errors.password && <span className="input-error">{errors.password.message}</span>}
          </div>
        </div>
      </div>

      {/* Section 2 : Physiologie & Objectifs */}
      <div className="flex flex-col gap-6">
        <h3 className="font-display text-2xl tracking-wide pb-2 mb-2">
          2. Physiologie & Objectifs
        </h3>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 gap-y-6">
          <div className="input-group mb-0">
            <span className="input-label">Sexe</span>
            <input type="hidden" {...register('sex', { required: 'Sexe requis' })} />
            <SexSelector
              value={sexValue}
              onChange={(val) => setValue('sex', val as "MALE" | "FEMALE", { shouldValidate: true })}
            />
            {errors.sex && <span className="input-error">{errors.sex.message}</span>}
          </div>

          <div className="input-group mb-0">
            <span className="input-label">Date de naissance</span>
            <input
              type="date"
              className="input-field h-[50px]"
              {...register('birthDate', {
                required: 'Date de naissance requise',
              })}
            />
            {errors.birthDate && <span className="input-error">{errors.birthDate.message}</span>}
          </div>

          <div className="input-group mb-0">
            <span className="input-label">Taille (cm)</span>
            <input
              type="number"
              className="input-field"
              placeholder="175"
              {...register('heightCm', {
                required: 'Taille requise',
                min: { value: 100, message: 'Minimum 100 cm' },
                max: { value: 250, message: 'Maximum 250 cm' },
                valueAsNumber: true,
              })}
            />
            {errors.heightCm && <span className="input-error">{errors.heightCm.message}</span>}
          </div>

          <div className="input-group mb-0">
            <span className="input-label">Masse grasse (% - Optionnel)</span>
            <input
              type="number"
              className="input-field"
              placeholder="Ex: 15.5"
              step="0.1"
              {...register('bodyFatPercentage', {
                min: { value: 3, message: 'Minimum 3%' },
                max: { value: 60, message: 'Maximum 60%' },
                valueAsNumber: true,
                validate: (val) => !val || !isNaN(val) || 'Doit être un nombre'
              })}
            />
            {errors.bodyFatPercentage && <span className="input-error">{errors.bodyFatPercentage.message}</span>}
          </div>

          <div className="input-group mb-0">
            <span className="input-label">Poids actuel (kg)</span>
            <input
              type="number"
              className="input-field"
              placeholder="85"
              step="0.1"
              {...register('currentWeightKg', { required: 'Poids actuel requis', valueAsNumber: true })}
            />
            {errors.currentWeightKg && <span className="input-error">{errors.currentWeightKg.message}</span>}
          </div>

          <div className="input-group mb-0">
            <span className="input-label">Poids cible (kg)</span>
            <input
              type="number"
              className="input-field"
              placeholder="75"
              step="0.1"
              {...register('targetWeightKg', { required: 'Poids cible requis', valueAsNumber: true })}
            />
            {errors.targetWeightKg && <span className="input-error">{errors.targetWeightKg.message}</span>}
          </div>

          <div className="input-group mb-0 lg:col-span-2">
            <span className="input-label">Niveau d'activité</span>
            <input type="hidden" {...register('activityLevel', { required: 'Niveau d\'activité requis' })} />
            <ActivityLevelSelector
              value={activityLevelValue}
              onChange={(val) => setValue('activityLevel', val as "SEDENTARY" | "MODERATELY_ACTIVE" | "VERY_ACTIVE", { shouldValidate: true })}
            />
            {errors.activityLevel && <span className="input-error">{errors.activityLevel.message}</span>}
          </div>

          <div className="input-group mt-2 lg:col-span-2 px-1">
            <div className="flex justify-between items-center mb-4">
              <span className="input-label m-0">Vitesse de progression</span>
              <span className="font-body text-[0.95rem] font-bold text-primary">
                {Number(weeklyRateKg) === 0
                  ? 'maintien du poids'
                  : `${Number(weeklyRateKg) > 0 ? '+' : ''}${Number(weeklyRateKg).toFixed(2)} kg/sem`}
              </span>
            </div>
            <div className="w-full">
              <input type="hidden" {...register('weeklyRateKg', { valueAsNumber: true })} />
              <input
                type="range"
                min={0}
                max={SLIDER_VALUES.length - 1}
                step={1}
                value={activeIndex}
                onChange={(e) => {
                  const val = SLIDER_VALUES[Number(e.target.value)];
                  setValue('weeklyRateKg', val, { shouldValidate: true });
                }}
                className="custom-range-slider w-full"
                style={{
                  background: `linear-gradient(90deg, var(--color-purple) 0%, var(--color-purple) ${(activeIndex) / (SLIDER_VALUES.length - 1) * 100}%, #E0E0E0 ${(activeIndex) / (SLIDER_VALUES.length - 1) * 100}%, #E0E0E0 100%)`,
                }}
              />
            </div>
            {errors.weeklyRateKg && <span className="input-error">{errors.weeklyRateKg.message}</span>}
          </div>
        </div>
      </div>

      <div className="flex flex-col items-center gap-4 mt-6 w-full">
        <button
          type="submit"
          className="button-save-objectif w-full max-w-[320px] mx-auto py-4 font-display text-xl tracking-wide"
          disabled={isSubmitting}
        >
          {isSubmitting ? 'INSCRIPTION...' : 'ENTRER'}
        </button>

        <p className="form-link m-0 text-center">
          Déjà un compte ? <Link to="/auth/login" className="text-black font-semibold hover:underline">Se connecter</Link>
        </p>
      </div>
    </form>
  );
};
