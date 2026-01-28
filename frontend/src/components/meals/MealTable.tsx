import { Link } from 'react-router-dom';
import type { MealLogResponse } from '../../types';
import MealCard from './MealCard';

interface MealTableProps {
  meals: MealLogResponse[];
}

export default function MealTable({ meals }: MealTableProps) {
  if (meals.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-400 text-lg">No meals logged yet.</p>
        <Link
          to="/meals/log"
          className="text-emerald-400 hover:text-emerald-300 text-sm font-medium mt-2 inline-block"
        >
          Log your first meal
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {meals.map((meal) => (
        <MealCard key={meal.id} meal={meal} />
      ))}
    </div>
  );
}
