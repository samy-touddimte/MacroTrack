import { createContext, useContext, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import type { AuthResponse, LoginRequest, RegisterRequest, User } from '../types';
import { getToken, getRefreshToken, setTokens as setAuthTokens, clearAuth } from '../utils/authUtils';
import { login as loginApi, register as registerApi, logoutApi } from '../services/authApi';
import { fetchCurrentUser } from '../services/userApi';
import { useCallback } from 'react';
import { useQueryClient } from '@tanstack/react-query';

interface AuthContextValue {
  user: User | null;
  isLoading: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  logout: () => Promise<void> | void;
  register: (payload: RegisterRequest) => Promise<void>;
  setUser: (user: User | null) => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);


export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const queryClient = useQueryClient();

  useEffect(() => {
    const loadUser = async () => {
      const token = getToken();
      if (!token) {
        setIsLoading(false);
        return;
      }

      try {
        const currentUser = await fetchCurrentUser();
        setUser(currentUser);
      } catch (error) {
        console.error("Échec du chargement de l'utilisateur:", error);
        clearAuth();
      } finally {
        setIsLoading(false);
      }
    };

    loadUser();
  }, []);


  const login = async (payload: LoginRequest) => {
    queryClient.clear();
    const response = await loginApi(payload);
    setTokens(response);
    const currentUser = await fetchCurrentUser();
    setUser(currentUser);
  };

  const register = async (payload: RegisterRequest) => {
    queryClient.clear();
    const response = await registerApi(payload);
    setTokens(response);
    const currentUser = await fetchCurrentUser();
    setUser(currentUser);
  };

  const setTokens = (response: AuthResponse) => {
    setAuthTokens(response.accessToken, response.refreshToken);
  };

  const logout = useCallback(async () => {
    const refreshTokenValue = getRefreshToken();
    if (refreshTokenValue) {
      try {
        await logoutApi(refreshTokenValue);
      } catch (error) {
        console.warn('Échec de la requête de déconnexion (logout) API :', error);
      }
    }
    clearAuth();
    setUser(null);
    queryClient.clear();
  }, [queryClient]);

  useEffect(() => {
    const forceLogoutOnSessionExpiry = () => {
      logout();
    };
    window.addEventListener('macrotrack:unauthorized', forceLogoutOnSessionExpiry);
    return () => window.removeEventListener('macrotrack:unauthorized', forceLogoutOnSessionExpiry);
  }, [logout]);

  return (
    <AuthContext.Provider value={{ user, isLoading, login, logout, register, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextValue => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
