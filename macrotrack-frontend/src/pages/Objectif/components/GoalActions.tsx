import React from 'react';

interface GoalActionsProps {
  goalExists: boolean;
  onOpenModal: () => void;
}

const GoalActions: React.FC<GoalActionsProps> = ({ goalExists, onOpenModal }) => {
  return (
    <div className="mt-6 w-full">
      <button
        onClick={onOpenModal}
        className="w-full py-3 px-4 rounded-full bg-[#F4F4F4] text-text-muted font-body font-bold text-xs uppercase tracking-wide hover:bg-primary hover:text-white transition-all duration-200 cursor-pointer border-none"
      >
        {goalExists ? "MODIFIER L'OBJECTIF" : "CRÉER UN OBJECTIF"}
      </button>
    </div>
  );
};

export default GoalActions;
