import { useState, useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import { useQuery } from '@tanstack/react-query';
import { format, subDays, addDays } from 'date-fns';
import { getWeightEntries } from '../../services/weightApi';
import { getFoodLogs } from '../../services/foodLogApi';
import ActionSheet from './ActionSheet';
import QuickAddForm from './QuickAddForm';
import WeightLogForm from './WeightLogForm';
import EditDayView from './EditDayView';

export type ActiveForm = null | 'quickAdd' | 'weightLog' | 'editDay';

interface LogEntryFlowProps {
  isOpen: boolean;
  onClose: () => void;
  plusClicks?: number;
}

const Z_TAB_BAR = 150;

const LogEntryFlow = ({ isOpen, onClose, plusClicks = 0 }: LogEntryFlowProps) => {
  const [activeForm, setActiveForm] = useState<ActiveForm>(null);
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [bottomOffset, setBottomOffset] = useState(0);
  
  const [isMounted, setIsMounted] = useState(false);
  const [isClosing, setIsClosing] = useState(true);

  const prevPlusClicks = useRef(plusClicks);

  useEffect(() => {
    if (isOpen && plusClicks > prevPlusClicks.current) {
      if (activeForm) {
        setActiveForm(null); // Return to action sheet
      } else {
        onClose(); // Close log entry flow
      }
    }
    prevPlusClicks.current = plusClicks;
  }, [plusClicks, isOpen, activeForm, onClose]);

  useEffect(() => {
    let timer: ReturnType<typeof setTimeout>;

    if (isOpen) {
      setIsMounted(true);
      setIsClosing(true);
      setActiveForm(null);
      setSelectedDate(new Date());
      setBottomOffset(100);

      timer = setTimeout(() => {
        setIsClosing(false);
      }, 10);
    } else {
      setIsClosing(true);
      timer = setTimeout(() => {
        setIsMounted(false);
      }, 280);
    }

    return () => clearTimeout(timer);
  }, [isOpen]);

  const startDateStr = format(subDays(selectedDate, 3), 'yyyy-MM-dd');
  const endDateStr = format(addDays(selectedDate, 3), 'yyyy-MM-dd');

  const { data: loggedDates = [] } = useQuery({
    queryKey: ['weekLogs', startDateStr, endDateStr],
    queryFn: async () => {
      const [weights, foodLogs] = await Promise.all([
        getWeightEntries(startDateStr, endDateStr),
        getFoodLogs(startDateStr, endDateStr)
      ]);
      const dates = new Set<string>();
      weights.forEach(w => dates.add(w.date));
      foodLogs.forEach(f => dates.add(f.date));
      return Array.from(dates);
    },
    enabled: isMounted
  });

  if (!isMounted) return null;

  return createPortal(
    <div 
      className="fixed inset-0"
      style={{ zIndex: Z_TAB_BAR - 1 }}
    >
      <div 
        className="absolute inset-0 bg-black/50 backdrop-blur-md z-0" 
        onClick={onClose}
        style={{ 
          opacity: isClosing ? 0 : 1,
          transition: 'opacity 280ms ease'
        }}
      />
      <div 
        className={`bottom-sheet z-10 ${isClosing ? 'bottom-sheet--leaving' : 'bottom-sheet--entering'}`}
        style={{
          bottom: 0,
          paddingBottom: `${bottomOffset}px`,
          transform: isClosing ? 'translateY(100%)' : 'translateY(0)'
        }}
      >
        <div 
          className="w-[36px] h-[4px] bg-[#E0E0E0] rounded-full mx-auto mt-2 mb-4 shrink-0 cursor-pointer" 
          onClick={onClose}
        />
        
        <div className="flex-1 overflow-y-auto px-6 pb-8 scrollbar-hide">
          {!activeForm && (
            <ActionSheet 
              selectedDate={selectedDate} 
              onSelectAction={setActiveForm} 
            />
          )}
          {activeForm === 'quickAdd' && (
            <QuickAddForm 
              selectedDate={selectedDate}
              onDateChange={setSelectedDate}
              onBack={() => setActiveForm(null)}
              onClose={onClose}
              loggedDates={loggedDates}
            />
          )}
          {activeForm === 'weightLog' && (
            <WeightLogForm 
              selectedDate={selectedDate}
              onDateChange={setSelectedDate}
              onBack={() => setActiveForm(null)}
              onClose={onClose}
              loggedDates={loggedDates}
            />
          )}
          {activeForm === 'editDay' && (
            <EditDayView
              selectedDate={selectedDate}
              onDateChange={setSelectedDate}
              onBack={() => setActiveForm(null)}
              loggedDates={loggedDates}
            />
          )}
        </div>
      </div>
    </div>,
    document.body
  );
};

export default LogEntryFlow;
