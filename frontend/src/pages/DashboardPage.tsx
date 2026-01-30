import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { useMyMeals } from '../hooks/useMeals';
import { useMyWorkouts } from '../hooks/useWorkouts';
import NutritionSummary from '../components/meals/NutritionSummary';
import MealCard from '../components/meals/MealCard';
import WorkoutCard from '../components/workouts/WorkoutCard';
import Card from '../components/ui/Card';
import LoadingSpinner from '../components/ui/LoadingSpinner';

export default function DashboardPage() {
  const { username } = useAuth();
  const { data: meals, isLoading: mealsLoading } = useMyMeals();
  const { data: workouts, isLoading: workoutsLoading } = useMyWorkouts();

  const isLoading = mealsLoading || workoutsLoading;
  const mostRecentWorkout = workouts?.[0];

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-white">Welcome back, {username}</h1>
        <p className="text-gray-400 mt-1">Here's your nutrition overview.</p>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : (
        <>
          <NutritionSummary meals={meals || []} />

          {/* Quick Actions */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Link to="/meals/log">
              <Card className="hover:border-emerald-500/50 transition-colors cursor-pointer">
                <div className="flex items-center gap-3">
                  <div className="bg-emerald-500/10 rounded-lg p-3">
                    <svg className="w-6 h-6 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-white">Log a Meal</p>
                    <p className="text-sm text-gray-400">Add food items and track macros</p>
                  </div>
                </div>
              </Card>
            </Link>
            <Link to="/workouts/log">
              <Card className="hover:border-emerald-500/50 transition-colors cursor-pointer">
                <div className="flex items-center gap-3">
                  <div className="bg-emerald-500/10 rounded-lg p-3">
                    <svg className="w-6 h-6 text-emerald-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                    </svg>
                  </div>
                  <div>
                    <p className="font-semibold text-white">Log a Workout</p>
                    <p className="text-sm text-gray-400">Track your exercises and progress</p>
                  </div>
                </div>
              </Card>
            </Link>
          </div>

          {/* Recent Workouts */}
          <div>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-white">Recent Workout</h2>
              {workouts && workouts.length > 0 && (
                <Link to="/workouts/history" className="text-sm text-emerald-400 hover:text-emerald-300">
                  View All
                </Link>
              )}
            </div>
            {mostRecentWorkout ? (
              <WorkoutCard workout={mostRecentWorkout} />
            ) : (
              <Card>
                <p className="text-gray-400 text-center py-4">
                  No workouts logged yet. Start by{' '}
                  <Link to="/workouts/log" className="text-emerald-400 hover:text-emerald-300">
                    logging your first workout
                  </Link>
                  .
                </p>
              </Card>
            )}
          </div>

          {/* Recent Meals */}
          <div>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-white">Recent Meals</h2>
              {meals && meals.length > 3 && (
                <Link to="/meals/history" className="text-sm text-emerald-400 hover:text-emerald-300">
                  View All
                </Link>
              )}
            </div>
            {meals && meals.length > 0 ? (
              <div className="space-y-4">
                {meals.slice(0, 3).map((meal) => (
                  <MealCard key={meal.id} meal={meal} />
                ))}
              </div>
            ) : (
              <Card>
                <p className="text-gray-400 text-center py-4">
                  No meals logged yet. Start by{' '}
                  <Link to="/meals/log" className="text-emerald-400 hover:text-emerald-300">
                    logging your first meal
                  </Link>
                  .
                </p>
              </Card>
            )}
          </div>
        </>
      )}
    </div>
  );
}
