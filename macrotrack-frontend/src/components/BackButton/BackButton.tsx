import { Link } from 'react-router-dom';

interface BackButtonProps {
  to?: string;
}

export const BackButton = ({ to = '/' }: BackButtonProps) => (
  <Link
    to={to}
    className="absolute top-6 left-6 md:top-8 md:left-8 flex items-center justify-center p-2 text-primary hover:opacity-80 transition-opacity"
    aria-label="Retour"
  >
    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round">
      <path d="m15 18-6-6 6-6"/>
    </svg>
  </Link>
);
