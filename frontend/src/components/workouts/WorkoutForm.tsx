import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCreateWorkout } from '../../hooks/useWorkouts';
import type { ExerciseItem, SetItem } from '../../types';
import ExerciseItemRow from './ExerciseItemRow';
import Button from '../ui/Button';

const emptyExercise = (): ExerciseItem => ({
  id: crypto.randomUUID(),
  name: '',
  category: null,
  durationMinutes: null,
  sets: null,
  reps: null,
  weight: null,
  caloriesBurned: null,
});

export default function WorkoutForm() {
  const [exercises, setExercises] = useState<ExerciseItem[]>([emptyExercise()]);
  const [error, setError] = useState('');
  const createWorkout = useCreateWorkout();
  const navigate = useNavigate();

  const handleChange = (index: number, field: keyof ExerciseItem, value: string | SetItem[]) => {
    setExercises((prev) => {
      const updated = [...prev];
      if (field === 'setDetails') {
        updated[index] = { ...updated[index], setDetails: value as SetItem[] };
      } else if (field === 'name' || field === 'category') {
        const strValue = value as string;
        updated[index] = { ...updated[index], [field]: strValue === '' ? null : strValue };
        if (field === 'name') {
          updated[index].name = strValue;
        }
      } else {
        const strValue = value as string;
        updated[index] = { ...updated[index], [field]: strValue === '' ? null : Number(strValue) };
      }
      return updated;
    });
  };

  const handleAdd = () => {
    setExercises((prev) => [...prev, emptyExercise()]);
  };

  const handleRemove = (index: number) => {
    setExercises((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const hasEmpty = exercises.some((ex) => !ex.name.trim());
    if (hasEmpty) {
      setError('Each exercise must have a name.');
      return;
    }

    createWorkout.mutate(
      { exercises },
      {
        onSuccess: () => {
          navigate('/workouts/history');
        },
        onError: () => {
          setError('Failed to log workout. Please try again.');
        },
      },
    );
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-3">
        {exercises.map((exercise, i) => (
          <ExerciseItemRow
            key={exercise.id}
            index={i}
            exercise={exercise}
            onChange={handleChange}
            onRemove={handleRemove}
            canRemove={exercises.length > 1}
          />
        ))}
      </div>

      <button
        type="button"
        onClick={handleAdd}
        className="text-emerald-400 hover:text-emerald-300 text-sm font-medium transition-colors"
      >
        + Add Exercise
      </button>

      {error && <p className="text-sm text-red-400">{error}</p>}

      <div className="pt-2">
        <Button type="submit" isLoading={createWorkout.isPending} className="w-full sm:w-auto">
          Log Workout
        </Button>
      </div>
    </form>
  );
}
