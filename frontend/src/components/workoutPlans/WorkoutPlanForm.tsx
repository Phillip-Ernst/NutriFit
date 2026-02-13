import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { WorkoutPlanDay, WorkoutPlanRequest, WorkoutPlanResponse } from '../../types';
import { useCreateWorkoutPlan, useUpdateWorkoutPlan } from '../../hooks/useWorkoutPlans';
import WorkoutPlanDayForm from './WorkoutPlanDayForm';
import Button from '../ui/Button';

interface WorkoutPlanFormProps {
  initialPlan?: WorkoutPlanResponse;
}

const emptyDay = (dayNumber: number): WorkoutPlanDay => ({
  clientId: crypto.randomUUID(),
  dayNumber,
  dayName: '',
  exercises: [],
});

export default function WorkoutPlanForm({ initialPlan }: WorkoutPlanFormProps) {
  const [name, setName] = useState(initialPlan?.name ?? '');
  const [description, setDescription] = useState(initialPlan?.description ?? '');
  const [days, setDays] = useState<WorkoutPlanDay[]>(
    initialPlan?.days.map((day) => ({
      ...day,
      clientId: day.clientId ?? crypto.randomUUID(),
      exercises: day.exercises.map((ex) => ({
        ...ex,
        id: ex.id ?? crypto.randomUUID(),
      })),
    })) ?? [emptyDay(1)]
  );
  const [error, setError] = useState('');

  const createPlan = useCreateWorkoutPlan();
  const updatePlan = useUpdateWorkoutPlan();
  const navigate = useNavigate();

  const isEditing = !!initialPlan;

  const handleAddDay = () => {
    setDays((prev) => [...prev, emptyDay(prev.length + 1)]);
  };

  const handleUpdateDay = (index: number, day: WorkoutPlanDay) => {
    setDays((prev) => {
      const updated = [...prev];
      updated[index] = day;
      return updated;
    });
  };

  const handleRemoveDay = (index: number) => {
    setDays((prev) => {
      const filtered = prev.filter((_, i) => i !== index);
      return filtered.map((day, i) => ({ ...day, dayNumber: i + 1 }));
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!name.trim()) {
      setError('Plan name is required.');
      return;
    }

    if (days.length === 0) {
      setError('Plan must have at least one day.');
      return;
    }

    const hasEmptyDayName = days.some((day) => !day.dayName.trim());
    if (hasEmptyDayName) {
      setError('Each day must have a name.');
      return;
    }

    const request: WorkoutPlanRequest = {
      name: name.trim(),
      description: description.trim() || null,
      days: days.map(({ dayNumber, dayName, exercises }) => ({
        dayNumber,
        dayName,
        exercises,
      })),
    };

    if (isEditing) {
      updatePlan.mutate(
        { id: initialPlan.id, data: request },
        {
          onSuccess: () => navigate(`/workouts/plans/${initialPlan.id}`),
          onError: () => setError('Failed to update plan. Please try again.'),
        }
      );
    } else {
      createPlan.mutate(request, {
        onSuccess: (plan) => navigate(`/workouts/plans/${plan.id}`),
        onError: () => setError('Failed to create plan. Please try again.'),
      });
    }
  };

  const isPending = createPlan.isPending || updatePlan.isPending;

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Plan Name</label>
          <input
            type="text"
            placeholder="e.g., PPL Split, Full Body"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-3 py-2 outline-none"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Description (optional)</label>
          <textarea
            placeholder="Brief description of this workout plan"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={2}
            className="w-full bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-3 py-2 outline-none resize-none"
          />
        </div>
      </div>

      <div className="space-y-4">
        <h3 className="text-lg font-semibold text-white">Workout Days</h3>
        {days.map((day, index) => (
          <WorkoutPlanDayForm
            key={day.clientId ?? day.id ?? `day-${index}`}
            day={day}
            onUpdate={(updated) => handleUpdateDay(index, updated)}
            onRemove={() => handleRemoveDay(index)}
            canRemove={days.length > 1}
          />
        ))}
      </div>

      <button
        type="button"
        onClick={handleAddDay}
        className="text-emerald-400 hover:text-emerald-300 text-sm font-medium transition-colors"
      >
        + Add Day
      </button>

      {error && <p className="text-sm text-red-400">{error}</p>}

      <div className="flex gap-3 pt-2">
        <Button type="submit" isLoading={isPending}>
          {isEditing ? 'Update Plan' : 'Create Plan'}
        </Button>
        <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
          Cancel
        </Button>
      </div>
    </form>
  );
}
