import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import PageTitle from '../../components/PageTitle/PageTitle';

import { useAuth } from '../../context/AuthContext';
import { ProfilForm } from './components/ProfilForm';
import { fetchCurrentUser, updateCurrentUser } from '../../services/userApi';
import { PasswordChangeForm } from './PasswordChangeForm';

import type { ProfilFormData } from '../../types';


const ProfilPage = () => {
  const { user, setUser } = useAuth();
  const queryClient = useQueryClient();
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [isPasswordFormOpen, setIsPasswordFormOpen] = useState(false);

  const { data: userData } = useQuery({
    queryKey: ['current-user'],
    queryFn: fetchCurrentUser
  });



  const updateMutation = useMutation({
    mutationFn: updateCurrentUser,
    onSuccess: (updatedUser) => {
      setUser(updatedUser);
      queryClient.setQueryData(['current-user'], updatedUser);
      setSuccessMessage('Profil mis à jour avec succès.');
    },
    onError: () => {
      setErrorMessage('Impossible de mettre à jour le profil. Vérifiez vos données.');
    }
  });



  const onSubmit = (data: ProfilFormData) => {
    setSuccessMessage('');
    setErrorMessage('');
    const updateData = {
      username: data.username,
      heightCm: data.heightCm,
      birthDate: data.birthDate,
      sex: data.sex,
      activityLevel: data.activityLevel,
    };

    updateMutation.mutate(updateData);
  };



  return (
    <main className="bg-white min-h-screen pb-24">
      <section className="w-full max-w-[600px] mx-auto py-8 px-5 text-black">
        <PageTitle title="MON PROFIL" />

        <div className="flex flex-col gap-12 mt-6">
          <div>
            <ProfilForm
              user={user}
              userData={userData}
              onSubmit={onSubmit}
              isPending={updateMutation.isPending}
              successMessage={successMessage}
              errorMessage={errorMessage}
            />
          </div>

          {/* Section Sécurité */}
          <div className="pt-2">
            <h3 className="font-display text-[1.5rem] tracking-wide m-0 mb-6">
              SÉCURITÉ
            </h3>
            <button
              type="button"
              onClick={() => setIsPasswordFormOpen(!isPasswordFormOpen)}
              className="button-save-objectif"
            >
              CHANGER LE MOT DE PASSE
            </button>
            
            <div 
              className="overflow-hidden transition-[max-height] duration-300 ease-in-out mt-4 w-full"
              style={{ maxHeight: isPasswordFormOpen ? '500px' : '0' }}
            >
              <PasswordChangeForm 
                onSuccess={setSuccessMessage} 
                onError={setErrorMessage} 
                onCancel={() => setIsPasswordFormOpen(false)}
              />
            </div>
          </div>
        </div>
      </section>
    </main>
  );
};

export default ProfilPage;
