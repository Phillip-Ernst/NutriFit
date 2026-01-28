interface MacroBarProps {
  protein: number;
  carbs: number;
  fats: number;
}

export default function MacroBar({ protein, carbs, fats }: MacroBarProps) {
  const total = protein + carbs + fats;
  if (total === 0) {
    return (
      <div className="w-full h-3 bg-gray-800 rounded-full overflow-hidden" />
    );
  }

  const pPct = (protein / total) * 100;
  const cPct = (carbs / total) * 100;
  const fPct = (fats / total) * 100;

  return (
    <div className="space-y-2">
      <div className="w-full h-3 bg-gray-800 rounded-full overflow-hidden flex">
        <div className="bg-blue-400 h-full" style={{ width: `${pPct}%` }} />
        <div className="bg-yellow-400 h-full" style={{ width: `${cPct}%` }} />
        <div className="bg-pink-400 h-full" style={{ width: `${fPct}%` }} />
      </div>
      <div className="flex gap-4 text-xs text-gray-400">
        <span className="flex items-center gap-1">
          <span className="w-2 h-2 rounded-full bg-blue-400 inline-block" />
          Protein {protein}g
        </span>
        <span className="flex items-center gap-1">
          <span className="w-2 h-2 rounded-full bg-yellow-400 inline-block" />
          Carbs {carbs}g
        </span>
        <span className="flex items-center gap-1">
          <span className="w-2 h-2 rounded-full bg-pink-400 inline-block" />
          Fats {fats}g
        </span>
      </div>
    </div>
  );
}
