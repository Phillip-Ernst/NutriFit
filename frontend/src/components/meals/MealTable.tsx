import { Link } from 'react-router-dom';
import type { MealLogResponse } from '../../types';
import MealCard from './MealCard';

interface MealTableProps {
  meals: MealLogResponse[];
  onDelete?: (id: number) => void;
  deletingId?: number | null;
}

export default function MealTable({ meals, onDelete, deletingId }: MealTableProps) {
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
        <MealCard
          key={meal.id}
          meal={meal}
          onDelete={onDelete ? () => onDelete(meal.id) : undefined}
          isDeleting={deletingId === meal.id}
        />
      ))}
    </div>
  );
}
