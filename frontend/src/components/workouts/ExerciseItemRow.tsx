import type { ExerciseItem } from '../../types';

interface ExerciseItemRowProps {
  index: number;
  exercise: ExerciseItem;
  onChange: (index: number, field: keyof ExerciseItem, value: string) => void;
  onRemove: (index: number) => void;
  canRemove: boolean;
}

export default function ExerciseItemRow({
  index,
  exercise,
  onChange,
  onRemove,
  canRemove,
}: ExerciseItemRowProps) {
  return (
    <div className="flex flex-col gap-2 p-3 bg-gray-800 rounded-lg">
      <div className="flex flex-col sm:flex-row gap-2">
        <input
          type="text"
          placeholder="Exercise name"
          value={exercise.name}
          onChange={(e) => onChange(index, 'name', e.target.value)}
          required
          className="flex-1 bg-gray-700 border border-gray-600 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-md px-3 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="text"
          placeholder="Category"
          value={exercise.category ?? ''}
          onChange={(e) => onChange(index, 'category', e.target.value)}
          className="sm:w-28 bg-gray-700 border border-gray-600 focus:border-gray-500 focus:ring-1 focus:ring-gray-500 text-white rounded-md px-3 py-2 text-sm outline-none placeholder:text-gray-500"
        />
      </div>
      <div className="flex flex-wrap gap-2">
        <input
          type="number"
          placeholder="Sets"
          value={exercise.sets ?? ''}
          onChange={(e) => onChange(index, 'sets', e.target.value)}
          min="0"
          className="w-16 bg-gray-700 border border-gray-600 focus:border-purple-400 focus:ring-1 focus:ring-purple-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="number"
          placeholder="Reps"
          value={exercise.reps ?? ''}
          onChange={(e) => onChange(index, 'reps', e.target.value)}
          min="0"
          className="w-16 bg-gray-700 border border-gray-600 focus:border-pink-400 focus:ring-1 focus:ring-pink-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="number"
          placeholder="Weight"
          value={exercise.weight ?? ''}
          onChange={(e) => onChange(index, 'weight', e.target.value)}
          min="0"
          className="w-20 bg-gray-700 border border-gray-600 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="number"
          placeholder="Mins"
          value={exercise.durationMinutes ?? ''}
          onChange={(e) => onChange(index, 'durationMinutes', e.target.value)}
          min="0"
          className="w-16 bg-gray-700 border border-gray-600 focus:border-blue-400 focus:ring-1 focus:ring-blue-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="number"
          placeholder="Cals"
          value={exercise.caloriesBurned ?? ''}
          onChange={(e) => onChange(index, 'caloriesBurned', e.target.value)}
          min="0"
          className="w-16 bg-gray-700 border border-gray-600 focus:border-orange-400 focus:ring-1 focus:ring-orange-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        {canRemove && (
          <button
            type="button"
            onClick={() => onRemove(index)}
            className="text-red-400 hover:text-red-300 transition-colors px-2"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
              />
            </svg>
          </button>
        )}
      </div>
    </div>
  );
}
