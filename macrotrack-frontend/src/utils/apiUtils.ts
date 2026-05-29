import axios from 'axios';

export function getErrorMessage(error: unknown, defaultMessage = "Une erreur est survenue"): string {
  if (axios.isAxiosError(error)) {
    return error.response?.data?.message || defaultMessage;
  }
  if (error instanceof Error) {
    return error.message;
  }
  return defaultMessage;
}
