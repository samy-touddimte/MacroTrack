import { useEffect, useState } from 'react';

import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { BackButton } from '../components/BackButton/BackButton';
import { useAuth } from '../context/AuthContext';
import type { LoginRequest } from '../types';
import { getErrorMessage } from '../utils/apiUtils';

const LoginPage = () => {
  const navigate = useNavigate();
  const { login, user, isLoading } = useAuth();
  const [apiError, setApiError] = useState('');
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginRequest>();

  useEffect(() => {
    if (!isLoading && user) {
      navigate('/dashboard');
    }
  }, [user, isLoading, navigate]);

  const onSubmit = async (data: LoginRequest) => {
    setApiError('');
    try {
      await login(data);
      navigate('/dashboard');
    } catch (error) {
      setApiError(getErrorMessage(error, 'Email ou mot de passe incorrect'));
    }
  };

  if (isLoading) {
    return <div className="min-h-screen bg-gray-light flex items-center justify-center">Chargement...</div>;
  }

  return (
    <main className="min-h-screen bg-gray-light flex flex-col items-center justify-center p-4 relative">
      <BackButton to="/" />

      <div className="w-full max-w-2xl bg-white rounded-3xl p-8 md:p-12">
        <h1 className="auth-title mb-8 text-center">CONNEXION</h1>

        <form 
          className="flex flex-col gap-6"
          onSubmit={handleSubmit(onSubmit)}
        >
          {apiError && <div className="form-error">{apiError}</div>}

          <div className="input-group mb-0">
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
            <span className="input-label">Mot de passe</span>
            <input
              type="password"
              className="input-field"
              placeholder="Entrez votre mot de passe"
              {...register('password', { required: 'Mot de passe requis' })}
            />
            {errors.password && <span className="input-error">{errors.password.message}</span>}
          </div>

          <button 
            type="submit" 
            className="button-save-objectif w-full py-4 font-display text-xl tracking-wide mt-4"
          >
            {isSubmitting ? 'CONNEXION...' : 'ENTRER'}
          </button>
        </form>

        <p className="form-link mt-8 text-center">
          Pas encore de compte ? <Link to="/auth/register">S'inscrire</Link>
        </p>
      </div>
    </main>
  );
};

export default LoginPage;
