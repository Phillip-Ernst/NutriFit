import { useMyMeals } from '../hooks/useMeals';
import MealTable from '../components/meals/MealTable';
import LoadingSpinner from '../components/ui/LoadingSpinner';

export default function MealHistoryPage() {
  const { data: meals, isLoading } = useMyMeals();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Meal History</h1>
        <p className="text-gray-400 mt-1">
          {meals ? `${meals.length} meal${meals.length !== 1 ? 's' : ''} logged` : 'Loading...'}
        </p>
      </div>
      {isLoading ? <LoadingSpinner /> : <MealTable meals={meals || []} />}
    </div>
  );
}
