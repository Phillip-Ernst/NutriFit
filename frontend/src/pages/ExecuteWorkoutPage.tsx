import { useParams, Link } from 'react-router-dom';
import { useWorkoutPlanDay } from '../hooks/useWorkoutPlans';
import Card from '../components/ui/Card';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import ExecuteWorkoutForm from '../components/workoutPlans/ExecuteWorkoutForm';

export default function ExecuteWorkoutPage() {
  const { dayId } = useParams<{ dayId: string }>();
  const dayIdNum = dayId ? Number(dayId) : undefined;

  const { data: planDay, isLoading, error } = useWorkoutPlanDay(dayIdNum);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error || !planDay) {
    return (
      <Card className="text-center py-8">
        <p className="text-red-400">Failed to load workout day.</p>
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
        <h1 className="text-2xl font-bold text-white">
          {planDay.dayName || `Day ${planDay.dayNumber}`}
        </h1>
        <p className="text-gray-400 mt-1">
          Log your actual performance for this workout.
        </p>
      </div>
      <Card>
        <ExecuteWorkoutForm planDay={planDay} />
      </Card>
    </div>
  );
}
