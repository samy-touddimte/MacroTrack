import React from 'react';

interface GoalActionsProps {
  goalExists: boolean;
  onOpenModal: () => void;
}

const GoalActions: React.FC<GoalActionsProps> = ({ goalExists, onOpenModal }) => {
  return (
    <div className="w-full flex justify-center mt-2">
      <button
        onClick={onOpenModal}
        className="py-4 px-12 rounded-full bg-[#F4F4F4] text-text-muted font-body font-bold text-[0.85rem] uppercase tracking-wide hover:bg-primary hover:text-white transition-all duration-200 cursor-pointer border-none"
      >
        {goalExists ? "MODIFIER L'OBJECTIF" : "CRÉER UN OBJECTIF"}
      </button>
    </div>
  );
};

export default GoalActions;
