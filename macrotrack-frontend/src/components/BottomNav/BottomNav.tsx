import { useState, useRef, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

interface BottomNavProps {
  onPlusClick: () => void;
}

const BottomNav = ({ onPlusClick }: BottomNavProps) => {
  const { user } = useAuth();

  return (
    <nav 
      className="bottom-nav-container fixed bottom-4 sm:bottom-6 left-1/2 -translate-x-1/2 w-[95%] min-w-[320px] sm:min-w-[550px] max-w-[850px] rounded-[1.5rem] sm:rounded-full shadow-none flex items-center justify-center py-2 sm:py-[0.6rem] px-2 sm:px-6 z-[150]"
    >

      {/* Côté Gauche */}
      <div className="flex flex-1 justify-evenly items-center gap-1 sm:gap-0">
        <div className="w-auto sm:w-[160px] flex justify-center">
          <NavItem to="/dashboard" label="Board" />
        </div>
        <div className="w-auto sm:w-[160px] flex justify-center">
          <NavItem to="/analytics" label="Analyses" />
        </div>
      </div>

      {/* bouton + central */}
      <button
        onClick={onPlusClick}
        className="nav-item-hover w-[44px] h-[44px] sm:w-[52px] sm:h-[52px] rounded-full bg-white text-primary border-none cursor-pointer text-[1.5rem] sm:text-[2rem] font-light flex items-center justify-center shrink-0 shadow-none mx-2 sm:mx-8 hover:bg-[#f2f2f2]"
        aria-label="Ajouter"
      >
        +
      </button>

      {/* Côté Droit */}
      <div className="flex flex-1 justify-evenly items-center gap-1 sm:gap-0">
        <div className="w-auto sm:w-[160px] flex justify-center">
          <NavItem to="/objectif" label="Objectif" />
        </div>
        <div className="w-auto sm:w-[160px] flex justify-center">
          <UserMenuItem username={user?.username} />
        </div>
      </div>
    </nav>
  );
};

const NavItem = ({ to, label }: { to: string; label: string }) => {
  const location = useLocation();
  const isActive = location.pathname === to;
  return (
    <Link to={to} 
      className={"nav-item-hover flex items-center justify-center py-1.5 sm:py-[0.6rem] px-2.5 sm:px-[1.2rem] rounded-full no-underline text-[0.8rem] sm:text-[1.45rem] font-display font-normal uppercase tracking-wide whitespace-nowrap " + (isActive ? "text-white bg-white/20" : "text-white bg-transparent hover:bg-white/10")}
    >
      <span>{label}</span>
    </Link>
  );
};

const UserMenuItem = ({ username }: { username?: string }) => {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();
  const { logout } = useAuth();
  const isActive = open;

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div ref={ref} className="relative">
      <button 
        onClick={() => setOpen(!open)} 
        className={"nav-item-hover flex items-center justify-center py-1.5 sm:py-[0.6rem] px-2.5 sm:px-[1.2rem] rounded-full border-none cursor-pointer text-[0.8rem] sm:text-[1.45rem] font-display font-normal uppercase tracking-wide whitespace-nowrap " + (isActive ? "text-white bg-white/20" : "text-white bg-transparent hover:bg-white/10")}
      >
        <span className="max-w-[70px] sm:max-w-[120px] overflow-hidden text-ellipsis whitespace-nowrap">
          {username ?? 'Compte'}
        </span>
      </button>

      {open && (
        <div className="absolute bottom-[calc(100%+12px)] -right-2 bg-white rounded-2xl p-2 min-w-[150px] border border-gray-light shadow-none z-[200]">
          <DropdownItem label="PROFIL" onClick={() => { navigate('/profil'); setOpen(false); }} />
          <DropdownItem label="CRÉDITS" onClick={() => { navigate('/credits'); setOpen(false); }} />
          <div className="h-[1px] bg-gray-border my-1" />
          <DropdownItem label="DÉCONNEXION" onClick={() => { logout(); navigate('/'); }} color="var(--color-purple)" />
        </div>
      )}
    </div>
  );
};

const DropdownItem = ({ label, onClick, color = 'var(--color-black)' }: { label: string; onClick: () => void; color?: string }) => (
  <button
    onClick={onClick}
    className="w-full text-left py-2 px-4 bg-transparent hover:bg-gray-light border-none cursor-pointer text-[0.85rem] font-display rounded-lg transition-colors"
    style={{ color }}
  >
    {label}
  </button>
);

export default BottomNav;
