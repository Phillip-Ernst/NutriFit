import { useState } from 'react';
import type { WorkoutLogResponse, ExerciseItem } from '../../types';
import Card from '../ui/Card';

function formatDate(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });
}

function isCardioExercise(category: string | null): boolean {
  return category?.toUpperCase() === 'CARDIO';
}

function hasSetDetails(exercise: ExerciseItem): boolean {
  return Boolean(exercise.setDetails && exercise.setDetails.length > 0);
}

interface WorkoutCardProps {
  workout: WorkoutLogResponse;
  onDelete?: () => void;
  isDeleting?: boolean;
}

export default function WorkoutCard({ workout, onDelete, isDeleting = false }: WorkoutCardProps) {
  const [expanded, setExpanded] = useState(false);

  const hasCardio = workout.exercises.some((ex) => isCardioExercise(ex.category));

  return (
    <Card className="space-y-3">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-gray-400">{formatDate(workout.createdAt)}</p>
          <p className="text-lg font-semibold text-white mt-1">
            {workout.workoutPlanDayName || 'Workout'}
          </p>
        </div>
        <div className="flex items-center gap-4">
          <div className="flex flex-wrap gap-3 text-sm text-gray-300">
            {hasCardio && workout.totalCaloriesBurned > 0 && (
              <span className="text-orange-400">{workout.totalCaloriesBurned} cal</span>
            )}
            {hasCardio && workout.totalDurationMinutes > 0 && (
              <span className="text-blue-400">{workout.totalDurationMinutes} min</span>
            )}
            <span className="text-purple-400">{workout.totalSets} sets</span>
            <span className="text-pink-400">{workout.totalReps} reps</span>
          </div>
          {onDelete && (
            <button
              onClick={onDelete}
              disabled={isDeleting}
              className="text-gray-500 hover:text-red-400 transition-colors disabled:opacity-50"
              aria-label="Delete workout"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          )}
        </div>
      </div>

      <button
        onClick={() => setExpanded(!expanded)}
        className="text-sm text-gray-400 hover:text-gray-200 transition-colors"
      >
        {expanded ? 'Hide' : 'Show'} {workout.exercises.length} exercise
        {workout.exercises.length !== 1 ? 's' : ''}
      </button>

      {expanded && (
        <div className="border-t border-gray-800 pt-3 space-y-3">
          {workout.exercises.map((exercise, i) => {
            const isCardio = isCardioExercise(exercise.category);
            const hasDetails = hasSetDetails(exercise);

            return (
              <div key={exercise.id ?? i} className="text-sm">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-1">
                  <div className="flex items-center gap-2">
                    <span className="text-gray-200 font-medium">{exercise.name}</span>
                    {exercise.category && (
                      <span className="text-xs text-gray-500 bg-gray-800 px-2 py-0.5 rounded">
                        {exercise.category}
                      </span>
                    )}
                  </div>
                  <div className="flex flex-wrap gap-2 text-gray-400">
                    {isCardio ? (
                      <>
                        {exercise.durationMinutes !== null && (
                          <span className="text-blue-400">{exercise.durationMinutes}min</span>
                        )}
                        {exercise.caloriesBurned !== null && (
                          <span className="text-orange-400">{exercise.caloriesBurned}cal</span>
                        )}
                      </>
                    ) : !hasDetails ? (
                      <>
                        {exercise.sets !== null && (
                          <span className="text-purple-400">{exercise.sets}s</span>
                        )}
                        {exercise.reps !== null && (
                          <span className="text-pink-400">{exercise.reps}r</span>
                        )}
                        {exercise.weight !== null && (
                          <span className="text-amber-400">{exercise.weight}lbs</span>
                        )}
                      </>
                    ) : (
                      <span className="text-gray-500">{exercise.setDetails!.length} sets</span>
                    )}
                  </div>
                </div>

                {hasDetails && (
                  <div className="mt-2 ml-4 border-l-2 border-gray-700 pl-3 space-y-1">
                    {exercise.setDetails!.map((set) => (
                      <div
                        key={set.id ?? set.setNumber}
                        className={`flex items-center gap-3 text-xs ${
                          set.completed === false ? 'text-gray-500' : 'text-gray-300'
                        }`}
                      >
                        <span className="text-gray-500 w-12">Set {set.setNumber}</span>
                        {set.reps !== null && (
                          <span className="text-pink-400">{set.reps} reps</span>
                        )}
                        {set.weight !== null && (
                          <span className="text-amber-400">{set.weight} lbs</span>
                        )}
                        {set.completed === false && (
                          <span className="text-gray-500 italic">skipped</span>
                        )}
                        {set.notes && (
                          <span className="text-gray-500 italic">{set.notes}</span>
                        )}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </Card>
  );
}
