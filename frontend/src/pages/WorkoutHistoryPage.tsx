import { useMyWorkouts } from '../hooks/useWorkouts';
import WorkoutTable from '../components/workouts/WorkoutTable';
import LoadingSpinner from '../components/ui/LoadingSpinner';

export default function WorkoutHistoryPage() {
  const { data: workouts, isLoading } = useMyWorkouts();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Workout History</h1>
        <p className="text-gray-400 mt-1">
          {workouts ? `${workouts.length} workout${workouts.length !== 1 ? 's' : ''} logged` : 'Loading...'}
        </p>
      </div>
      {isLoading ? <LoadingSpinner /> : <WorkoutTable workouts={workouts || []} />}
    </div>
  );
}
