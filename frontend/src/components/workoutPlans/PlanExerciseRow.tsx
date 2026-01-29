import type { WorkoutPlanExercise } from '../../types';

interface PlanExerciseRowProps {
  index: number;
  exercise: WorkoutPlanExercise;
  onChange: (index: number, field: keyof WorkoutPlanExercise, value: string | number | null) => void;
  onRemove: (index: number) => void;
  canRemove: boolean;
}

export default function PlanExerciseRow({
  index,
  exercise,
  onChange,
  onRemove,
  canRemove,
}: PlanExerciseRowProps) {
  return (
    <div className="flex flex-col gap-2 p-3 bg-gray-800 rounded-lg">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-white font-medium">{exercise.name}</span>
          {exercise.category && (
            <span className="text-xs text-gray-500 bg-gray-700 px-2 py-0.5 rounded">
              {exercise.category}
            </span>
          )}
          {exercise.isCustom && (
            <span className="text-xs text-emerald-500 bg-emerald-900/30 px-2 py-0.5 rounded">
              Custom
            </span>
          )}
        </div>
        {canRemove && (
          <button
            type="button"
            onClick={() => onRemove(index)}
            className="text-red-400 hover:text-red-300 transition-colors"
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
      <div className="flex flex-wrap gap-2">
        <div className="flex items-center gap-1">
          <label className="text-xs text-gray-500">Sets:</label>
          <input
            type="number"
            placeholder="0"
            value={exercise.targetSets ?? ''}
            onChange={(e) =>
              onChange(index, 'targetSets', e.target.value === '' ? null : Number(e.target.value))
            }
            min="0"
            className="w-14 bg-gray-700 border border-gray-600 focus:border-purple-400 focus:ring-1 focus:ring-purple-400 text-white rounded-md px-2 py-1 text-sm outline-none"
          />
        </div>
        <div className="flex items-center gap-1">
          <label className="text-xs text-gray-500">Reps:</label>
          <input
            type="number"
            placeholder="0"
            value={exercise.targetReps ?? ''}
            onChange={(e) =>
              onChange(index, 'targetReps', e.target.value === '' ? null : Number(e.target.value))
            }
            min="0"
            className="w-14 bg-gray-700 border border-gray-600 focus:border-pink-400 focus:ring-1 focus:ring-pink-400 text-white rounded-md px-2 py-1 text-sm outline-none"
          />
        </div>
        <div className="flex items-center gap-1">
          <label className="text-xs text-gray-500">Weight:</label>
          <input
            type="number"
            placeholder="0"
            value={exercise.targetWeight ?? ''}
            onChange={(e) =>
              onChange(index, 'targetWeight', e.target.value === '' ? null : Number(e.target.value))
            }
            min="0"
            className="w-16 bg-gray-700 border border-gray-600 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 text-white rounded-md px-2 py-1 text-sm outline-none"
          />
        </div>
      </div>
    </div>
  );
}
