import type { SetItem } from '../../types';

interface SetRowProps {
  setItem: SetItem;
  onChange: (field: keyof SetItem, value: string | boolean) => void;
  onRemove: () => void;
  canRemove: boolean;
}

export default function SetRow({
  setItem,
  onChange,
  onRemove,
  canRemove,
}: SetRowProps) {
  return (
    <div className="flex items-center gap-2 pl-4 py-1">
      <span className="text-xs text-gray-500 w-12">Set {setItem.setNumber}</span>
      <input
        type="number"
        placeholder="Reps"
        value={setItem.reps ?? ''}
        onChange={(e) => onChange('reps', e.target.value)}
        min="0"
        className="w-16 bg-gray-700 border border-gray-600 focus:border-pink-400 focus:ring-1 focus:ring-pink-400 text-white rounded-md px-2 py-1.5 text-sm outline-none placeholder:text-gray-500"
      />
      <input
        type="number"
        placeholder="Weight"
        value={setItem.weight ?? ''}
        onChange={(e) => onChange('weight', e.target.value)}
        min="0"
        className="w-20 bg-gray-700 border border-gray-600 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 text-white rounded-md px-2 py-1.5 text-sm outline-none placeholder:text-gray-500"
      />
      <label className="flex items-center gap-1 text-xs text-gray-400">
        <input
          type="checkbox"
          checked={setItem.completed ?? true}
          onChange={(e) => onChange('completed', e.target.checked)}
          className="w-4 h-4 rounded bg-gray-700 border-gray-600 text-emerald-500 focus:ring-emerald-500"
        />
        Done
      </label>
      {canRemove && (
        <button
          type="button"
          onClick={onRemove}
          className="text-red-400 hover:text-red-300 transition-colors px-1"
          aria-label="Remove set"
        >
          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      )}
    </div>
  );
}
