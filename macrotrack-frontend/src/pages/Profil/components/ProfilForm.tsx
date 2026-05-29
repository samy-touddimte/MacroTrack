import { useForm } from 'react-hook-form';
import { SexSelector } from '../../../components/SexSelector/SexSelector';
import { ActivityLevelSelector } from '../../../components/ActivityLevelSelector/ActivityLevelSelector';
import { useEffect } from 'react';
import type { User, ProfilFormData } from '../../../types';

interface ProfilFormProps {
  user: User | null;
  userData: User | null | undefined;
  onSubmit: (data: ProfilFormData) => void;
  isPending: boolean;
  successMessage: string;
  errorMessage: string;
}

export const ProfilForm = ({ user, userData, onSubmit, isPending, successMessage, errorMessage }: ProfilFormProps) => {
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<ProfilFormData>();

  const sexValue = watch('sex');
  const activityLevelValue = watch('activityLevel');

  useEffect(() => {
    if (userData) {
      setValue('email', userData.email);
      setValue('username', userData.username || '');
      setValue('heightCm', userData.heightCm || 170);
      setValue('birthDate', userData.birthDate || '');
      setValue('sex', userData.sex || '');
      setValue('activityLevel', userData.activityLevel || '');
    }
  }, [userData, setValue]);

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {successMessage && (
        <div className="bg-[#E8F5E9] border border-[#A5D6A7] rounded-xl px-4 py-3 mb-6 text-[#2E7D32] text-[0.9rem] font-semibold font-body">
          {successMessage}
        </div>
      )}
      {errorMessage && (
        <div className="form-error mb-6 font-body">
          {errorMessage}
        </div>
      )}

      <div className="flex flex-col gap-8">
        <div className="flex flex-col gap-6 py-2">
          <h3 className="font-display text-[1.5rem] tracking-wide m-0">
            INFORMATIONS PERSONNELLES
          </h3>

          <div className="grid gap-5">
            <div className="input-group">
              <label className="input-label">Email</label>
              <div className="input-field text-text-muted">
                {user?.email}
              </div>
            </div>

            <div className="input-group">
              <label className="input-label">Nom d'utilisateur</label>
              <input
                type="text"
                className="input-field"
                {...register('username', {
                  required: "Nom d'utilisateur requis",
                  minLength: { value: 3, message: 'Minimum 3 caractères' }
                })}
              />
              {errors.username && <span className="input-error font-body text-[0.8rem] text-[#D62828] mt-1 block">{errors.username.message}</span>}
            </div>

            <div className="input-group">
              <label className="input-label">Taille (cm)</label>
              <input
                type="number"
                className="input-field"
                {...register('heightCm', {
                  required: 'Taille requise',
                  min: { value: 100, message: 'Minimum 100cm' },
                  max: { value: 250, message: 'Maximum 250cm' },
                  valueAsNumber: true
                })}
              />
              {errors.heightCm && <span className="input-error font-body text-[0.8rem] text-[#D62828] mt-1 block">{errors.heightCm.message}</span>}
            </div>

            <div className="input-group">
              <label className="input-label">Date de naissance</label>
              <input
                type="date"
                className="input-field"
                {...register('birthDate')}
              />
            </div>

            <div className="input-group">
              <label className="input-label">Sexe</label>
              <input type="hidden" {...register('sex')} />
              <SexSelector
                value={sexValue || ''}
                onChange={(val: string) => setValue('sex', val, { shouldValidate: true, shouldDirty: true })}
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-6 py-2">
          <h3 className="font-display text-[1.5rem] tracking-wide m-0">
            ACTIVITÉ
          </h3>

          <div className="grid gap-5">
            <div className="input-group">
              <label className="input-label">Niveau d'activité</label>
              <input type="hidden" {...register('activityLevel')} />
              <ActivityLevelSelector
                value={activityLevelValue || ''}
                onChange={(val: string) => setValue('activityLevel', val, { shouldValidate: true, shouldDirty: true })}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="flex justify-center mt-10">
        <button
          type="submit"
          disabled={isPending}
          className="button-save-objectif"
        >
          {isPending ? 'ENREGISTREMENT...' : 'ENREGISTRER LES MODIFICATIONS'}
        </button>
      </div>
    </form>
  );
};
