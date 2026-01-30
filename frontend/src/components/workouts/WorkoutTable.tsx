import { Link } from 'react-router-dom';
import type { WorkoutLogResponse } from '../../types';
import WorkoutCard from './WorkoutCard';

interface WorkoutTableProps {
  workouts: WorkoutLogResponse[];
}

export default function WorkoutTable({ workouts }: WorkoutTableProps) {
  if (workouts.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-400 text-lg">No workouts logged yet.</p>
        <Link
          to="/workouts/log"
          className="text-emerald-400 hover:text-emerald-300 text-sm font-medium mt-2 inline-block"
        >
          Log your first workout
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {workouts.map((workout) => (
        <WorkoutCard key={workout.id} workout={workout} />
      ))}
    </div>
  );
}
