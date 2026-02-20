import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { WorkoutPlanDay, ExerciseItem, SetItem } from '../../types';
import { useCreateWorkoutFromPlan } from '../../hooks/useWorkoutPlans';
import Button from '../ui/Button';
import SetRow from '../workouts/SetRow';

interface ExecuteWorkoutFormProps {
  planDay: WorkoutPlanDay;
}

function createSetDetails(targetSets: number | null, targetReps: number | null, targetWeight: number | null): SetItem[] {
  const numSets = targetSets ?? 1;
  const setDetails: SetItem[] = [];
  for (let i = 0; i < numSets; i++) {
    setDetails.push({
      id: crypto.randomUUID(),
      setNumber: i + 1,
      reps: targetReps,
      weight: targetWeight,
      completed: false,
    });
  }
  return setDetails;
}

export default function ExecuteWorkoutForm({ planDay }: ExecuteWorkoutFormProps) {
  const [exercises, setExercises] = useState<ExerciseItem[]>(
    planDay.exercises.map((ex) => {
      const isCardio = ex.category?.toUpperCase() === 'CARDIO';
      return {
        id: ex.id ?? crypto.randomUUID(),
        name: ex.name,
        category: ex.category,
        sets: ex.targetSets,
        reps: ex.targetReps,
        weight: ex.targetWeight,
        durationMinutes: null,
        caloriesBurned: null,
        setDetails: isCardio ? undefined : createSetDetails(ex.targetSets, ex.targetReps, ex.targetWeight),
      };
    })
  );
  const [error, setError] = useState('');

  const createWorkout = useCreateWorkoutFromPlan();
  const navigate = useNavigate();

  const handleSetChange = (
    exerciseIndex: number,
    setIndex: number,
    field: keyof SetItem,
    value: string | boolean
  ) => {
    setExercises((prev) => {
      const updated = [...prev];
      const exercise = updated[exerciseIndex];
      if (!exercise.setDetails) return prev;

      const newSetDetails = [...exercise.setDetails];
      if (field === 'completed') {
        newSetDetails[setIndex] = { ...newSetDetails[setIndex], [field]: value as boolean };
      } else if (field === 'reps' || field === 'weight') {
        newSetDetails[setIndex] = {
          ...newSetDetails[setIndex],
          [field]: value === '' ? null : Number(value),
        };
      }
      updated[exerciseIndex] = { ...exercise, setDetails: newSetDetails };
      return updated;
    });
  };

  const handleRemoveSet = (exerciseIndex: number, setIndex: number) => {
    setExercises((prev) => {
      const updated = [...prev];
      const exercise = updated[exerciseIndex];
      if (!exercise.setDetails) return prev;

      const newSetDetails = exercise.setDetails
        .filter((_, i) => i !== setIndex)
        .map((set, i) => ({ ...set, setNumber: i + 1 }));
      updated[exerciseIndex] = { ...exercise, setDetails: newSetDetails };
      return updated;
    });
  };

  const handleAddSet = (exerciseIndex: number) => {
    setExercises((prev) => {
      const updated = [...prev];
      const exercise = updated[exerciseIndex];
      const setDetails = exercise.setDetails ?? [];
      const lastSet = setDetails[setDetails.length - 1];

      const newSetDetails = [
        ...setDetails,
        {
          id: crypto.randomUUID(),
          setNumber: setDetails.length + 1,
          reps: lastSet?.reps ?? null,
          weight: lastSet?.weight ?? null,
          completed: false,
        },
      ];
      updated[exerciseIndex] = { ...exercise, setDetails: newSetDetails };
      return updated;
    });
  };

  const handleCardioChange = (
    index: number,
    field: 'durationMinutes' | 'caloriesBurned',
    value: string
  ) => {
    setExercises((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value === '' ? null : Number(value) };
      return updated;
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!planDay.id) {
      setError('Invalid workout plan day.');
      return;
    }

    const hasEmpty = exercises.some((ex) => !ex.name.trim());
    if (hasEmpty) {
      setError('Each exercise must have a name.');
      return;
    }

    createWorkout.mutate(
      {
        workoutPlanDayId: planDay.id,
        exercises,
      },
      {
        onSuccess: () => navigate('/workouts/history'),
        onError: () => setError('Failed to log workout. Please try again.'),
      }
    );
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-3">
        {exercises.map((exercise, i) => (
          <div key={exercise.id} className="p-4 bg-gray-800 rounded-lg space-y-3">
            <div className="flex items-center gap-2">
              <span className="text-white font-medium">{exercise.name}</span>
              {exercise.category && (
                <span className="text-xs text-gray-500 bg-gray-700 px-2 py-0.5 rounded">
                  {exercise.category}
                </span>
              )}
            </div>

            {exercise.category?.toUpperCase() === 'CARDIO' ? (
              <div className="flex flex-wrap gap-3">
                <div>
                  <label className="block text-xs text-gray-500 mb-1">Duration (mins)</label>
                  <input
                    type="number"
                    value={exercise.durationMinutes ?? ''}
                    onChange={(e) => handleCardioChange(i, 'durationMinutes', e.target.value)}
                    min="0"
                    className="w-28 bg-gray-700 border border-gray-600 focus:border-blue-400 focus:ring-1 focus:ring-blue-400 text-white rounded-md px-2 py-1.5 text-sm outline-none"
                  />
                </div>
                <div>
                  <label className="block text-xs text-gray-500 mb-1">Calories Burned</label>
                  <input
                    type="number"
                    value={exercise.caloriesBurned ?? ''}
                    onChange={(e) => handleCardioChange(i, 'caloriesBurned', e.target.value)}
                    min="0"
                    className="w-28 bg-gray-700 border border-gray-600 focus:border-orange-400 focus:ring-1 focus:ring-orange-400 text-white rounded-md px-2 py-1.5 text-sm outline-none"
                  />
                </div>
              </div>
            ) : (
              <div className="space-y-1 border-l-2 border-gray-700">
                {exercise.setDetails?.map((setItem, setIndex) => (
                  <SetRow
                    key={setItem.id}
                    setItem={setItem}
                    onChange={(field, value) => handleSetChange(i, setIndex, field, value)}
                    onRemove={() => handleRemoveSet(i, setIndex)}
                    canRemove={(exercise.setDetails?.length ?? 0) > 1}
                  />
                ))}
                <div className="pl-4 py-1">
                  <button
                    type="button"
                    onClick={() => handleAddSet(i)}
                    className="text-xs text-emerald-400 hover:text-emerald-300 transition-colors"
                  >
                    + Add Set
                  </button>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>

      {exercises.length === 0 && (
        <p className="text-gray-500 text-center py-4">
          No exercises in this workout day.
        </p>
      )}

      {error && <p className="text-sm text-red-400">{error}</p>}

      <div className="flex gap-3 pt-2">
        <Button type="submit" isLoading={createWorkout.isPending} disabled={exercises.length === 0}>
          Log Workout
        </Button>
        <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
          Cancel
        </Button>
      </div>
    </form>
  );
}
