import { Link } from 'react-router-dom';
import { useMyWorkouts } from '../hooks/useWorkouts';
import Card from '../components/ui/Card';
import StatCard from '../components/ui/StatCard';
import WorkoutCard from '../components/workouts/WorkoutCard';
import LoadingSpinner from '../components/ui/LoadingSpinner';

export default function WorkoutsPage() {
  const { data: workouts, isLoading } = useMyWorkouts();

  const totalWorkouts = workouts?.length ?? 0;
  const totalMinutes = workouts?.reduce((sum, w) => sum + w.totalDurationMinutes, 0) ?? 0;
  const totalCalories = workouts?.reduce((sum, w) => sum + w.totalCaloriesBurned, 0) ?? 0;
  const recentWorkouts = workouts?.slice(0, 3) ?? [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Workouts</h1>
        <p className="text-gray-400 mt-1">Track and manage your workout sessions.</p>
      </div>

      <div className="flex flex-wrap gap-3">
        <Link
          to="/workouts/log"
          className="inline-flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2 rounded-lg font-medium transition-colors"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Log Workout
        </Link>
        <Link
          to="/workouts/history"
          className="inline-flex items-center gap-2 bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded-lg font-medium transition-colors"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          View History
        </Link>
        <Link
          to="/workouts/plans"
          className="inline-flex items-center gap-2 bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded-lg font-medium transition-colors"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          My Plans
        </Link>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard label="Total Workouts" value={totalWorkouts} color="emerald" />
        <StatCard label="Total Minutes" value={totalMinutes} unit="min" color="blue" />
        <StatCard label="Calories Burned" value={totalCalories} unit="cal" color="orange" />
      </div>

      <div>
        <h2 className="text-lg font-semibold text-white mb-4">Recent Workouts</h2>
        {isLoading ? (
          <LoadingSpinner />
        ) : recentWorkouts.length === 0 ? (
          <Card className="text-center py-8">
            <p className="text-gray-400">No workouts logged yet.</p>
            <Link
              to="/workouts/log"
              className="text-emerald-400 hover:text-emerald-300 text-sm font-medium mt-2 inline-block"
            >
              Log your first workout
            </Link>
          </Card>
        ) : (
          <div className="space-y-4">
            {recentWorkouts.map((workout) => (
              <WorkoutCard key={workout.id} workout={workout} />
            ))}
            {totalWorkouts > 3 && (
              <Link
                to="/workouts/history"
                className="block text-center text-emerald-400 hover:text-emerald-300 text-sm font-medium py-2"
              >
                View all {totalWorkouts} workouts
              </Link>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
