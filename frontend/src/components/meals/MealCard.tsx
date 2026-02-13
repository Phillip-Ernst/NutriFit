import { useState } from 'react';
import type { MealLogResponse } from '../../types';
import Card from '../ui/Card';
import MacroBar from '../ui/MacroBar';

function safe(val: number | null | undefined): number {
  return val ?? 0;
}

function formatDate(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  });
}

interface MealCardProps {
  meal: MealLogResponse;
  onDelete?: () => void;
  isDeleting?: boolean;
}

export default function MealCard({ meal, onDelete, isDeleting = false }: MealCardProps) {
  const [expanded, setExpanded] = useState(false);

  return (
    <Card className="space-y-3">
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-gray-400">{formatDate(meal.createdAt)}</p>
          <p className="text-lg font-semibold text-white mt-1">
            {meal.totalCalories} <span className="text-sm font-normal text-gray-400">kcal</span>
          </p>
        </div>
        <div className="flex items-center gap-4">
          <div className="flex gap-3 text-sm text-gray-300">
            <span className="text-blue-400">{meal.totalProtein}g P</span>
            <span className="text-yellow-400">{meal.totalCarbs}g C</span>
            <span className="text-pink-400">{meal.totalFats}g F</span>
          </div>
          {onDelete && (
            <button
              onClick={onDelete}
              disabled={isDeleting}
              className="text-gray-500 hover:text-red-400 transition-colors disabled:opacity-50"
              aria-label="Delete meal"
            >
              <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          )}
        </div>
      </div>

      <MacroBar
        protein={meal.totalProtein}
        carbs={meal.totalCarbs}
        fats={meal.totalFats}
      />

      <button
        onClick={() => setExpanded(!expanded)}
        className="text-sm text-gray-400 hover:text-gray-200 transition-colors"
      >
        {expanded ? 'Hide' : 'Show'} {meal.foods.length} food item{meal.foods.length !== 1 ? 's' : ''}
      </button>

      {expanded && (
        <div className="border-t border-gray-800 pt-3 space-y-2">
          {meal.foods.map((food, i) => (
            <div key={food.id ?? `${meal.id}-food-${i}`} className="flex items-center justify-between text-sm">
              <span className="text-gray-200">{food.type}</span>
              <span className="text-gray-400">
                {safe(food.calories)} cal | {safe(food.protein)}p | {safe(food.carbs)}c | {safe(food.fats)}f
              </span>
            </div>
          ))}
        </div>
      )}
    </Card>
  );
}
