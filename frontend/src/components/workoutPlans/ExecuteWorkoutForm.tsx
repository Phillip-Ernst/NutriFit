import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { WorkoutPlanDay, ExerciseItem } from '../../types';
import { useCreateWorkoutFromPlan } from '../../hooks/useWorkoutPlans';
import Button from '../ui/Button';

interface ExecuteWorkoutFormProps {
  planDay: WorkoutPlanDay;
}

export default function ExecuteWorkoutForm({ planDay }: ExecuteWorkoutFormProps) {
  const [exercises, setExercises] = useState<ExerciseItem[]>(
    planDay.exercises.map((ex) => ({
      name: ex.name,
      category: ex.category,
      sets: ex.targetSets,
      reps: ex.targetReps,
      weight: ex.targetWeight,
      durationMinutes: null,
      caloriesBurned: null,
    }))
  );
  const [error, setError] = useState('');

  const createWorkout = useCreateWorkoutFromPlan();
  const navigate = useNavigate();

  const handleChange = (
    index: number,
    field: keyof ExerciseItem,
    value: string
  ) => {
    setExercises((prev) => {
      const updated = [...prev];
      if (field === 'name' || field === 'category') {
        updated[index] = { ...updated[index], [field]: value === '' ? null : value };
        if (field === 'name') {
          updated[index].name = value;
        }
      } else {
        updated[index] = { ...updated[index], [field]: value === '' ? null : Number(value) };
      }
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
          <div key={i} className="p-4 bg-gray-800 rounded-lg space-y-3">
            <div className="flex items-center gap-2">
              <span className="text-white font-medium">{exercise.name}</span>
              {exercise.category && (
                <span className="text-xs text-gray-500 bg-gray-700 px-2 py-0.5 rounded">
                  {exercise.category}
                </span>
              )}
            </div>

            <div className="flex flex-wrap gap-3">
              <div>
                <label className="block text-xs text-gray-500 mb-1">Sets</label>
                <input
                  type="number"
                  value={exercise.sets ?? ''}
                  onChange={(e) => handleChange(i, 'sets', e.target.value)}
                  min="0"
                  className="w-16 bg-gray-700 border border-gray-600 focus:border-purple-400 focus:ring-1 focus:ring-purple-400 text-white rounded-md px-2 py-1.5 text-sm outline-none"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">Reps</label>
                <input
                  type="number"
                  value={exercise.reps ?? ''}
                  onChange={(e) => handleChange(i, 'reps', e.target.value)}
                  min="0"
                  className="w-16 bg-gray-700 border border-gray-600 focus:border-pink-400 focus:ring-1 focus:ring-pink-400 text-white rounded-md px-2 py-1.5 text-sm outline-none"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">Weight</label>
                <input
                  type="number"
                  value={exercise.weight ?? ''}
                  onChange={(e) => handleChange(i, 'weight', e.target.value)}
                  min="0"
                  className="w-20 bg-gray-700 border border-gray-600 focus:border-amber-400 focus:ring-1 focus:ring-amber-400 text-white rounded-md px-2 py-1.5 text-sm outline-none"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">Mins</label>
                <input
                  type="number"
                  value={exercise.durationMinutes ?? ''}
                  onChange={(e) => handleChange(i, 'durationMinutes', e.target.value)}
                  min="0"
                  className="w-16 bg-gray-700 border border-gray-600 focus:border-blue-400 focus:ring-1 focus:ring-blue-400 text-white rounded-md px-2 py-1.5 text-sm outline-none"
                />
              </div>
              <div>
                <label className="block text-xs text-gray-500 mb-1">Cals</label>
                <input
                  type="number"
                  value={exercise.caloriesBurned ?? ''}
                  onChange={(e) => handleChange(i, 'caloriesBurned', e.target.value)}
                  min="0"
                  className="w-16 bg-gray-700 border border-gray-600 focus:border-orange-400 focus:ring-1 focus:ring-orange-400 text-white rounded-md px-2 py-1.5 text-sm outline-none"
                />
              </div>
            </div>
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
