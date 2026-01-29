import { Link, useParams, useNavigate } from 'react-router-dom';
import { useWorkoutPlan, useDeleteWorkoutPlan } from '../hooks/useWorkoutPlans';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import LoadingSpinner from '../components/ui/LoadingSpinner';

function formatDate(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, {
    month: 'long',
    day: 'numeric',
    year: 'numeric',
  });
}

export default function PlanDetailPage() {
  const { id } = useParams<{ id: string }>();
  const planId = id ? Number(id) : undefined;
  const navigate = useNavigate();

  const { data: plan, isLoading, error } = useWorkoutPlan(planId);
  const deletePlan = useDeleteWorkoutPlan();

  const handleDelete = () => {
    if (!planId) return;
    if (!window.confirm('Are you sure you want to delete this plan?')) return;

    deletePlan.mutate(planId, {
      onSuccess: () => navigate('/workouts/plans'),
    });
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error || !plan) {
    return (
      <Card className="text-center py-8">
        <p className="text-red-400">Failed to load workout plan.</p>
        <Link
          to="/workouts/plans"
          className="text-emerald-400 hover:text-emerald-300 text-sm font-medium mt-2 inline-block"
        >
          Back to plans
        </Link>
      </Card>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">{plan.name}</h1>
          {plan.description && <p className="text-gray-400 mt-1">{plan.description}</p>}
          <p className="text-sm text-gray-500 mt-2">Created {formatDate(plan.createdAt)}</p>
        </div>
        <div className="flex gap-2">
          <Link to={`/workouts/plans/${plan.id}/edit`}>
            <Button variant="secondary" size="sm">
              Edit
            </Button>
          </Link>
          <Button variant="danger" size="sm" onClick={handleDelete} isLoading={deletePlan.isPending}>
            Delete
          </Button>
        </div>
      </div>

      <div className="space-y-4">
        <h2 className="text-lg font-semibold text-white">
          Workout Days ({plan.days.length})
        </h2>

        {plan.days.map((day) => (
          <Card key={day.id ?? day.dayNumber} className="space-y-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <span className="text-emerald-400 font-semibold">Day {day.dayNumber}</span>
                <span className="text-white font-medium">{day.dayName}</span>
              </div>
              {day.id && (
                <Link to={`/workouts/execute/${day.id}`}>
                  <Button size="sm">Start Workout</Button>
                </Link>
              )}
            </div>

            {day.exercises.length === 0 ? (
              <p className="text-gray-500 text-sm">No exercises in this day.</p>
            ) : (
              <div className="space-y-2">
                {day.exercises.map((exercise, i) => (
                  <div
                    key={i}
                    className="flex flex-col sm:flex-row sm:items-center sm:justify-between text-sm gap-1 p-2 bg-gray-800 rounded"
                  >
                    <div className="flex items-center gap-2">
                      <span className="text-gray-200 font-medium">{exercise.name}</span>
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
                    <div className="flex flex-wrap gap-2 text-gray-400">
                      {exercise.targetSets !== null && (
                        <span className="text-purple-400">{exercise.targetSets} sets</span>
                      )}
                      {exercise.targetReps !== null && (
                        <span className="text-pink-400">{exercise.targetReps} reps</span>
                      )}
                      {exercise.targetWeight !== null && (
                        <span className="text-amber-400">{exercise.targetWeight} lbs</span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        ))}
      </div>

      <Link
        to="/workouts/plans"
        className="inline-block text-emerald-400 hover:text-emerald-300 text-sm font-medium"
      >
        &larr; Back to plans
      </Link>
    </div>
  );
}
