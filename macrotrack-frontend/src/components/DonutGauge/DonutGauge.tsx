interface DonutGaugeProps {
  value: number;
  target: number;
  size?: number;
  strokeWidth?: number;
}

function polarToCartesian(cx: number, cy: number, r: number, deg: number) {
  const rad = (deg - 90) * Math.PI / 180;
  return { x: cx + r * Math.cos(rad), y: cy + r * Math.sin(rad) };
}

function describeArc(cx: number, cy: number, r: number, startDeg: number, endDeg: number) {
  const start = polarToCartesian(cx, cy, r, endDeg);
  const end = polarToCartesian(cx, cy, r, startDeg);
  const large = endDeg - startDeg <= 180 ? '0' : '1';
  return `M ${start.x} ${start.y} A ${r} ${r} 0 ${large} 0 ${end.x} ${end.y}`;
}

const DonutGauge = ({ value, target, size = 320, strokeWidth = 24 }: DonutGaugeProps) => {
  const radius = (size / 2) - strokeWidth;
  const percent = Math.min(1, Math.max(0, (value ?? 0) / (target ?? 1)));

  const startAngle = -110;
  const endAngle = 110;
  const totalAngle = endAngle - startAngle;
  const currentAngle = startAngle + percent * totalAngle;
  const visualHeight = 240; // hauteur visuelle réduite

  return (
    <div className="w-full flex flex-col items-center">
      <div className="flex items-center justify-center w-full gap-1 md:gap-10 scale-90 md:scale-100 relative" style={{ height: visualHeight }}>
        {/* Valeur restante (Desktop uniquement) */}
        <div className="hidden md:block donut-side-value translate-y-[40px]">
          <p className="donut-side-number">
            {Math.max(0, Math.round((target ?? 0) - (value ?? 0)))}
          </p>
          <p className="donut-side-label">Restantes</p>
        </div>

        {/* SVG de l'arc + texte central */}
        <div className="donut-center-wrapper" style={{ width: size, height: visualHeight }}>
          <svg
            width={size}
            height={size}
            viewBox={`0 0 ${size} ${size}`}
            className="absolute top-[15px]"
          >
            <path
              d={describeArc(size / 2, size / 2, radius, startAngle, endAngle)}
              fill="none"
              stroke="var(--color-gray-light)"
              strokeWidth={strokeWidth}
              strokeLinecap="round"
            />
            <path
              d={describeArc(size / 2, size / 2, radius, startAngle, currentAngle)}
              fill="none"
              stroke="var(--color-purple)"
              strokeWidth={strokeWidth}
              strokeLinecap="round"
            />
          </svg>

          {/* Calories consommées */}
          <div className="donut-center-value translate-y-[50px]">
            <p className="donut-center-number">
              {Math.round(value ?? 0)}
            </p>
            <p className="donut-center-label">Consommées</p>
          </div>
        </div>

        {/* Cible journalière (Desktop uniquement) */}
        <div className="hidden md:block donut-side-value translate-y-[40px]">
          <p className="donut-side-number">
            {Math.round(target ?? 0)}
          </p>
          <p className="donut-side-label">Cible</p>
        </div>
      </div>

      {/* Valeurs sous le donut (Mobile uniquement) */}
      <div className="flex md:hidden justify-between w-full max-w-[300px] mt-2 mb-2 px-4">
        <div className="donut-side-value text-center">
          <p className="donut-side-number">
            {Math.max(0, Math.round((target ?? 0) - (value ?? 0)))}
          </p>
          <p className="donut-side-label">Restantes</p>
        </div>
        <div className="donut-side-value text-center">
          <p className="donut-side-number">
            {Math.round(target ?? 0)}
          </p>
          <p className="donut-side-label">Cible</p>
        </div>
      </div>
    </div>
  );
};

export default DonutGauge;