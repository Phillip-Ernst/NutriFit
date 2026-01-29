import { Link } from 'react-router-dom';
import type { WorkoutPlanResponse } from '../../types';
import Card from '../ui/Card';

function formatDate(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  });
}

export default function WorkoutPlanCard({ plan }: { plan: WorkoutPlanResponse }) {
  const totalExercises = plan.days.reduce((sum, day) => sum + day.exercises.length, 0);

  return (
    <Link to={`/workouts/plans/${plan.id}`}>
      <Card className="hover:border-emerald-500/50 transition-colors cursor-pointer">
        <div className="flex items-start justify-between">
          <div>
            <h3 className="text-lg font-semibold text-white">{plan.name}</h3>
            {plan.description && (
              <p className="text-sm text-gray-400 mt-1">{plan.description}</p>
            )}
          </div>
          <span className="text-xs text-gray-500">{formatDate(plan.createdAt)}</span>
        </div>

        <div className="mt-3 flex flex-wrap gap-2">
          <span className="text-sm text-emerald-400">
            {plan.days.length} day{plan.days.length !== 1 ? 's' : ''}
          </span>
          <span className="text-gray-600">|</span>
          <span className="text-sm text-gray-400">
            {totalExercises} exercise{totalExercises !== 1 ? 's' : ''}
          </span>
        </div>

        <div className="mt-3 flex flex-wrap gap-2">
          {plan.days.map((day) => (
            <span
              key={day.id ?? day.dayNumber}
              className="text-xs bg-gray-800 text-gray-300 px-2 py-1 rounded"
            >
              {day.dayName || `Day ${day.dayNumber}`}
            </span>
          ))}
        </div>
      </Card>
    </Link>
  );
}
