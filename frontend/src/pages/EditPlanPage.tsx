import { useParams, Link } from 'react-router-dom';
import { useWorkoutPlan } from '../hooks/useWorkoutPlans';
import Card from '../components/ui/Card';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import WorkoutPlanForm from '../components/workoutPlans/WorkoutPlanForm';

export default function EditPlanPage() {
  const { id } = useParams<{ id: string }>();
  const planId = id ? Number(id) : undefined;

  const { data: plan, isLoading, error } = useWorkoutPlan(planId);

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
      <div>
        <h1 className="text-2xl font-bold text-white">Edit Workout Plan</h1>
        <p className="text-gray-400 mt-1">Update your workout plan details and exercises.</p>
      </div>
      <Card>
        <WorkoutPlanForm initialPlan={plan} />
      </Card>
    </div>
  );
}
