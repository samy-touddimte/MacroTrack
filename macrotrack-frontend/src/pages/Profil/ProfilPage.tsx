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
      <section className="container section max-w-[800px] mx-auto py-8 px-5 text-black">
        <div className="mb-8">
          <PageTitle title="MON PROFIL" />
        </div>

        <ProfilForm
          user={user}
          userData={userData}
          onSubmit={onSubmit}
          isPending={updateMutation.isPending}
          successMessage={successMessage}
          errorMessage={errorMessage}
        />

        {/* Section Sécurité */}
        <div className="mt-16 pt-12">
          <h3 className="font-display text-[1.5rem] tracking-wide m-0 mb-6">
            SÉCURITÉ
          </h3>
          <button
            type="button"
            onClick={() => setIsPasswordFormOpen(!isPasswordFormOpen)}
            className="w-full max-w-[400px] py-3 bg-primary text-white font-display text-[1.1rem] rounded-xl hover:bg-opacity-80 transition-all cursor-pointer border-none text-center"
          >
            CHANGER LE MOT DE PASSE
          </button>
          
          <div 
            className="overflow-hidden transition-[max-height] duration-300 ease-in-out mt-4 max-w-[400px]"
            style={{ maxHeight: isPasswordFormOpen ? '500px' : '0' }}
          >
            <PasswordChangeForm 
              onSuccess={setSuccessMessage} 
              onError={setErrorMessage} 
              onCancel={() => setIsPasswordFormOpen(false)}
            />
          </div>
        </div>
      </section>
    </main>
  );
};

export default ProfilPage;
