import { ReactNode, useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import BottomNav from '../components/BottomNav/BottomNav';
import LogEntryFlow from '../components/LogEntryFlow/LogEntryFlow';


interface AuthenticatedLayoutProps {
  children: ReactNode;
}

const AuthenticatedLayout = ({ children }: AuthenticatedLayoutProps) => {
  const [calendarOpen, setCalendarOpen] = useState(false);
  const [plusClicks, setPlusClicks] = useState(0);
  const location = useLocation();

  useEffect(() => {
    setCalendarOpen(false);
  }, [location.pathname]);

  return (
    <div>
      <main className="pb-[100px] mx-auto w-[95%] min-w-[550px] max-w-[850px] px-6">
        {children}
      </main>
      
      <BottomNav onPlusClick={() => {
        if (!calendarOpen) {
          setCalendarOpen(true);
        } else {
          setPlusClicks(c => c + 1);
        }
      }} />

      <LogEntryFlow 
        isOpen={calendarOpen} 
        onClose={() => setCalendarOpen(false)} 
        plusClicks={plusClicks}
      />
    </div>
  );
};

export default AuthenticatedLayout;
