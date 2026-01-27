import type { MealLogResponse } from '../../types';
import StatCard from '../ui/StatCard';
import MacroBar from '../ui/MacroBar';

function isToday(dateStr: string): boolean {
  const meal = new Date(dateStr);
  const now = new Date();
  return meal.toDateString() === now.toDateString();
}

interface NutritionSummaryProps {
  meals: MealLogResponse[];
}

export default function NutritionSummary({ meals }: NutritionSummaryProps) {
  const todayMeals = meals.filter((m) => isToday(m.createdAt));

  const totals = todayMeals.reduce(
    (acc, m) => ({
      calories: acc.calories + m.totalCalories,
      protein: acc.protein + m.totalProtein,
      carbs: acc.carbs + m.totalCarbs,
      fats: acc.fats + m.totalFats,
    }),
    { calories: 0, protein: 0, carbs: 0, fats: 0 },
  );

  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold text-white">Today's Nutrition</h2>
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <StatCard label="Calories" value={totals.calories} unit="kcal" color="border-orange-400" />
        <StatCard label="Protein" value={totals.protein} unit="g" color="border-blue-400" />
        <StatCard label="Carbs" value={totals.carbs} unit="g" color="border-yellow-400" />
        <StatCard label="Fats" value={totals.fats} unit="g" color="border-pink-400" />
      </div>
      <MacroBar protein={totals.protein} carbs={totals.carbs} fats={totals.fats} />
      <p className="text-sm text-gray-500">
        {todayMeals.length} meal{todayMeals.length !== 1 ? 's' : ''} logged today
      </p>
    </div>
  );
}
