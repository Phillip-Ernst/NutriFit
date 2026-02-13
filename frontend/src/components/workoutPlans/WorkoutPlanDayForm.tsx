import { useState } from 'react';
import type { WorkoutPlanDay, WorkoutPlanExercise } from '../../types';
import PlanExerciseRow from './PlanExerciseRow';
import ExercisePicker from './ExercisePicker';

interface WorkoutPlanDayFormProps {
  day: WorkoutPlanDay;
  onUpdate: (day: WorkoutPlanDay) => void;
  onRemove: () => void;
  canRemove: boolean;
}

export default function WorkoutPlanDayForm({
  day,
  onUpdate,
  onRemove,
  canRemove,
}: WorkoutPlanDayFormProps) {
  const [isPickerOpen, setIsPickerOpen] = useState(false);

  const handleDayNameChange = (name: string) => {
    onUpdate({ ...day, dayName: name });
  };

  const handleAddExercise = (exercise: WorkoutPlanExercise) => {
    onUpdate({ ...day, exercises: [...day.exercises, exercise] });
  };

  const handleExerciseChange = (
    index: number,
    field: keyof WorkoutPlanExercise,
    value: string | number | null
  ) => {
    const updated = [...day.exercises];
    updated[index] = { ...updated[index], [field]: value };
    onUpdate({ ...day, exercises: updated });
  };

  const handleRemoveExercise = (index: number) => {
    onUpdate({ ...day, exercises: day.exercises.filter((_, i) => i !== index) });
  };

  return (
    <div className="border border-gray-800 rounded-xl p-4 space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <span className="text-emerald-400 font-semibold">Day {day.dayNumber}</span>
          <input
            type="text"
            placeholder="Day name (e.g., Push, Legs)"
            value={day.dayName}
            onChange={(e) => handleDayNameChange(e.target.value)}
            className="bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-3 py-1.5 text-sm outline-none"
          />
        </div>
        {canRemove && (
          <button
            type="button"
            onClick={onRemove}
            className="text-red-400 hover:text-red-300 transition-colors text-sm"
          >
            Remove Day
          </button>
        )}
      </div>

      <div className="space-y-2">
        {day.exercises.length === 0 ? (
          <p className="text-gray-500 text-sm text-center py-4">No exercises added yet</p>
        ) : (
          day.exercises.map((exercise, i) => (
            <PlanExerciseRow
              key={exercise.id ?? `${day.clientId ?? day.id ?? day.dayNumber}-exercise-${i}`}
              index={i}
              exercise={exercise}
              onChange={handleExerciseChange}
              onRemove={handleRemoveExercise}
              canRemove={true}
            />
          ))
        )}
      </div>

      <button
        type="button"
        onClick={() => setIsPickerOpen(true)}
        className="text-emerald-400 hover:text-emerald-300 text-sm font-medium transition-colors"
      >
        + Add Exercise
      </button>

      <ExercisePicker
        isOpen={isPickerOpen}
        onClose={() => setIsPickerOpen(false)}
        onSelect={handleAddExercise}
      />
    </div>
  );
}
