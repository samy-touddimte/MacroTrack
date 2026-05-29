import { STORAGE_KEYS } from '../constants/storage';

export function getToken() {
  return localStorage.getItem(STORAGE_KEYS.TOKEN);
}

export function getRefreshToken() {
  return localStorage.getItem(STORAGE_KEYS.REFRESH);
}

export function setTokens(accessToken: string, refreshToken?: string) {
  localStorage.setItem(STORAGE_KEYS.TOKEN, accessToken);
  if (refreshToken) {
    localStorage.setItem(STORAGE_KEYS.REFRESH, refreshToken);
  }
}

export function clearAuth() {
  localStorage.removeItem(STORAGE_KEYS.TOKEN);
  localStorage.removeItem(STORAGE_KEYS.REFRESH);
}
