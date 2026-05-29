import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BackButton } from '../../components/BackButton/BackButton';
import { useAuth } from '../../context/AuthContext';
import type { RegisterRequest } from '../../types';
import { getErrorMessage } from '../../utils/apiUtils';
import { RegisterForm } from './components/RegisterForm';

const RegisterPage = () => {
  const navigate = useNavigate();
  const { register: registerUser, user, isLoading } = useAuth();
  const [apiError, setApiError] = useState('');

  useEffect(() => {
    if (!isLoading && user) {
      navigate('/dashboard');
    }
  }, [user, isLoading, navigate]);

  const onSubmit = async (data: RegisterRequest) => {
    setApiError('');
    try {
      await registerUser(data);
      navigate('/dashboard');
    } catch (error) {
      setApiError(getErrorMessage(error, "Erreur lors de l'inscription"));
    }
  };

  if (isLoading) {
    return <div className="min-h-screen bg-gray-light flex items-center justify-center">Chargement...</div>;
  }

  return (
    <main className="min-h-screen bg-gray-light flex flex-col items-center justify-center p-4 py-12 relative">
      <BackButton to="/" />

      <div className="w-full max-w-[900px] bg-white rounded-3xl p-6 md:p-10 lg:p-12 my-8">
        <h1 className="auth-title mb-10 text-center">INSCRIPTION</h1>
        <RegisterForm onSubmit={onSubmit} apiError={apiError} />
      </div>
    </main>
  );
};

export default RegisterPage;
