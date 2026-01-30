import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useMyWorkoutPlans } from '../hooks/useWorkoutPlans';
import WorkoutForm from '../components/workouts/WorkoutForm';
import Card from '../components/ui/Card';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import type { WorkoutPlanResponse } from '../types';

export default function WorkoutLogPage() {
  const { data: plans, isLoading } = useMyWorkoutPlans();
  const [selectedPlan, setSelectedPlan] = useState<WorkoutPlanResponse | null>(null);
  const navigate = useNavigate();

  const handleSelectPlan = (plan: WorkoutPlanResponse) => {
    setSelectedPlan(selectedPlan?.id === plan.id ? null : plan);
  };

  const handleStartDay = (dayId: number) => {
    navigate(`/workouts/execute/${dayId}`);
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Log a Workout</h1>
        <p className="text-gray-400 mt-1">Choose from your plans or log a quick workout.</p>
      </div>

      {/* Workout Plans Section */}
      <div>
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-lg font-semibold text-white">My Workout Plans</h2>
          <Link
            to="/workouts/plans/new"
            className="text-sm text-emerald-400 hover:text-emerald-300"
          >
            + Create Plan
          </Link>
        </div>

        {isLoading ? (
          <LoadingSpinner />
        ) : plans && plans.length > 0 ? (
          <div className="space-y-3">
            {plans.map((plan) => (
              <div key={plan.id}>
                <button
                  type="button"
                  onClick={() => handleSelectPlan(plan)}
                  className={`w-full text-left p-4 rounded-lg border transition-colors ${
                    selectedPlan?.id === plan.id
                      ? 'bg-gray-800 border-emerald-500'
                      : 'bg-gray-900 border-gray-800 hover:border-gray-700'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="font-semibold text-white">{plan.name}</p>
                      <p className="text-sm text-gray-400">
                        {plan.days.length} day{plan.days.length !== 1 ? 's' : ''}
                      </p>
                    </div>
                    <svg
                      className={`w-5 h-5 text-gray-400 transition-transform ${
                        selectedPlan?.id === plan.id ? 'rotate-180' : ''
                      }`}
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M19 9l-7 7-7-7"
                      />
                    </svg>
                  </div>
                </button>

                {selectedPlan?.id === plan.id && (
                  <div className="mt-2 ml-4 space-y-2">
                    {plan.days.map((day) => (
                      <button
                        key={day.id}
                        type="button"
                        onClick={() => day.id && handleStartDay(day.id)}
                        className="w-full text-left p-3 rounded-lg bg-gray-800 hover:bg-gray-750 border border-gray-700 hover:border-emerald-500/50 transition-colors"
                      >
                        <div className="flex items-center justify-between">
                          <div>
                            <p className="font-medium text-white">{day.dayName}</p>
                            <p className="text-xs text-gray-500">
                              {day.exercises.length} exercise{day.exercises.length !== 1 ? 's' : ''}
                            </p>
                          </div>
                          <span className="text-emerald-400 text-sm font-medium">Start</span>
                        </div>
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <Card>
            <p className="text-gray-400 text-center py-4">
              No workout plans yet.{' '}
              <Link to="/workouts/plans/new" className="text-emerald-400 hover:text-emerald-300">
                Create your first plan
              </Link>{' '}
              to get started.
            </p>
          </Card>
        )}
      </div>

      {/* Divider */}
      <div className="flex items-center gap-4">
        <div className="flex-1 border-t border-gray-800" />
        <span className="text-sm text-gray-500">or</span>
        <div className="flex-1 border-t border-gray-800" />
      </div>

      {/* Quick Log Section */}
      <div>
        <h2 className="text-lg font-semibold text-white mb-3">Quick Log</h2>
        <Card>
          <WorkoutForm />
        </Card>
      </div>
    </div>
  );
}
