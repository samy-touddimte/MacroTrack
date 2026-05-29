import { useForm } from 'react-hook-form';
import { useMutation } from '@tanstack/react-query';
import { changePassword } from '../../services/authApi';

interface PasswordFormData {
  currentPassword?: string;
  newPassword?: string;
  confirmNewPassword?: string;
}

interface PasswordChangeFormProps {
  onSuccess?: (msg: string) => void;
  onError?: (msg: string) => void;
  onCancel?: () => void;
}

export const PasswordChangeForm = ({ onSuccess, onError, onCancel }: PasswordChangeFormProps) => {
  const { register, handleSubmit, formState: { errors }, reset } = useForm<PasswordFormData>();

  const passwordMutation = useMutation({
    mutationFn: changePassword,
    onSuccess: () => {
      onSuccess?.('Mot de passe mis à jour avec succès.');
      reset();
      onCancel?.(); // close form on success
    },
    onError: () => {
      onError?.("Impossible de changer le mot de passe. Vérifiez l'ancien mot de passe.");
    }
  });

  const onSubmit = (data: PasswordFormData) => {
    if (data.newPassword !== data.confirmNewPassword) {
      onError?.('Les mots de passe ne correspondent pas.');
      return;
    }
    passwordMutation.mutate({
      currentPassword: data.currentPassword,
      newPassword: data.newPassword,
    });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-5 max-w-[400px]">
      <div className="input-group">
        <label className="input-label">Ancien mot de passe</label>
        <input
          type="password"
          className="input-field"
          {...register('currentPassword', { required: 'Requis' })}
        />
        {errors.currentPassword && <span className="input-error font-body text-[0.8rem] text-[#D62828] mt-1 block">{errors.currentPassword.message}</span>}
      </div>

      <div className="input-group">
        <label className="input-label">Nouveau mot de passe</label>
        <input
          type="password"
          className="input-field"
          {...register('newPassword', {
            required: 'Requis',
            minLength: { value: 6, message: 'Minimum 6 caractères' },
            pattern: {
              value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/,
              message: 'Doit contenir >=6 caractères, maj, min, chiffre'
            },
          })}
        />
        {errors.newPassword && <span className="input-error font-body text-[0.8rem] text-[#D62828] mt-1 block">{errors.newPassword.message}</span>}
      </div>

      <div className="input-group">
        <label className="input-label">Confirmer le mot de passe</label>
        <input
          type="password"
          className="input-field"
          {...register('confirmNewPassword', { required: 'Requis' })}
        />
        {errors.confirmNewPassword && <span className="input-error font-body text-[0.8rem] text-[#D62828] mt-1 block">{errors.confirmNewPassword.message}</span>}
      </div>

      <div className="flex gap-3 mt-4">
        <button
          type="button"
          onClick={onCancel}
          className="flex-1 py-3 bg-gray-light text-black font-display text-[1.1rem] rounded-xl hover:bg-[#E8E8E8] transition-all cursor-pointer border-none"
        >
          ANNULER
        </button>
        <button
          type="submit"
          disabled={passwordMutation.isPending}
          className="flex-1 py-3 bg-primary text-white font-display text-[1.1rem] rounded-xl hover:bg-opacity-80 transition-all cursor-pointer border-none"
        >
          {passwordMutation.isPending ? 'MISE À JOUR...' : 'ENREGISTRER'}
        </button>
      </div>
    </form>
  );
};
