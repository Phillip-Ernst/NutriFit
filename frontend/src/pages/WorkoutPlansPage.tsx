import { Link } from 'react-router-dom';
import { useMyWorkoutPlans } from '../hooks/useWorkoutPlans';
import Card from '../components/ui/Card';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import WorkoutPlanCard from '../components/workoutPlans/WorkoutPlanCard';

export default function WorkoutPlansPage() {
  const { data: plans, isLoading } = useMyWorkoutPlans();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Workout Plans</h1>
        <p className="text-gray-400 mt-1">Create and manage reusable workout templates.</p>
      </div>

      <div className="flex flex-wrap gap-3">
        <Link
          to="/workouts/plans/new"
          className="inline-flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2 rounded-lg font-medium transition-colors"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Create New Plan
        </Link>
      </div>

      <div>
        <h2 className="text-lg font-semibold text-white mb-4">
          My Plans {plans && plans.length > 0 && `(${plans.length})`}
        </h2>
        {isLoading ? (
          <LoadingSpinner />
        ) : plans?.length === 0 ? (
          <Card className="text-center py-8">
            <p className="text-gray-400">No workout plans created yet.</p>
            <Link
              to="/workouts/plans/new"
              className="text-emerald-400 hover:text-emerald-300 text-sm font-medium mt-2 inline-block"
            >
              Create your first plan
            </Link>
          </Card>
        ) : (
          <div className="space-y-4">
            {plans?.map((plan) => (
              <WorkoutPlanCard key={plan.id} plan={plan} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
