import React from 'react';

const ADHERENCE_MESSAGES: Record<number, string> = {
  5: "tu suis ta diète parfaitement",
  4: "très bon respect de la diète",
  3: "respect correct, quelques écarts",
  2: "plusieurs jours hors quota",
  1: "diète peu respectée",
  0: "quota non respecté",
  '-1': "disponible après 3 jours de logs depuis le début de l'objectif",
};

interface DietAdherenceProps {
  score: number;
  scoreColor: string;
}

const DietAdherence: React.FC<DietAdherenceProps> = ({ score, scoreColor }) => {
  return (
    <div className="bg-white rounded-2xl p-5 mt-4">
      <div className="flex justify-between items-center mb-3">
        <span className="font-body text-xs text-text-muted uppercase tracking-wide">
          RESPECT DE LA DIÈTE
        </span>
        {score !== -1 && (
          <span className="font-display text-[18px] text-black tracking-wide">
            {score}/5
          </span>
        )}
      </div>

      <div className="flex gap-[6px] mb-3">
        {Array.from({ length: 5 }).map((_, i) => {
          const active = score !== -1 && i < score;
          const fillBg = score === -1 ? 'var(--color-gray-mid)' : active ? scoreColor : 'var(--color-gray-mid)';
          return (
            <div
              key={i}
              className="flex-1 h-2 rounded-[4px] transition-colors duration-300 ease-in-out"
              style={{ background: fillBg }}
            />
          );
        })}
      </div>

      <div className="font-body text-[11px] text-text-muted italic">
        {ADHERENCE_MESSAGES[score] || "disponible après 3 jours de logs depuis le début de l'objectif"}
      </div>
    </div>
  );
};

export default DietAdherence;
