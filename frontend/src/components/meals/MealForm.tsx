import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCreateMeal } from '../../hooks/useMeals';
import type { FoodItem } from '../../types';
import FoodItemRow from './FoodItemRow';
import Button from '../ui/Button';

const emptyFood = (): FoodItem => ({
  type: '',
  calories: null,
  protein: null,
  carbs: null,
  fats: null,
});

export default function MealForm() {
  const [foods, setFoods] = useState<FoodItem[]>([emptyFood()]);
  const [error, setError] = useState('');
  const createMeal = useCreateMeal();
  const navigate = useNavigate();

  const handleChange = (index: number, field: keyof FoodItem, value: string) => {
    setFoods((prev) => {
      const updated = [...prev];
      if (field === 'type') {
        updated[index] = { ...updated[index], type: value };
      } else {
        updated[index] = { ...updated[index], [field]: value === '' ? null : Number(value) };
      }
      return updated;
    });
  };

  const handleAdd = () => {
    setFoods((prev) => [...prev, emptyFood()]);
  };

  const handleRemove = (index: number) => {
    setFoods((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const hasEmpty = foods.some((f) => !f.type.trim());
    if (hasEmpty) {
      setError('Each food item must have a name.');
      return;
    }

    createMeal.mutate(
      { foods },
      {
        onSuccess: () => {
          navigate('/meals/history');
        },
        onError: () => {
          setError('Failed to log meal. Please try again.');
        },
      },
    );
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-3">
        {foods.map((food, i) => (
          <FoodItemRow
            key={i}
            index={i}
            food={food}
            onChange={handleChange}
            onRemove={handleRemove}
            canRemove={foods.length > 1}
          />
        ))}
      </div>

      <button
        type="button"
        onClick={handleAdd}
        className="text-emerald-400 hover:text-emerald-300 text-sm font-medium transition-colors"
      >
        + Add Food Item
      </button>

      {error && <p className="text-sm text-red-400">{error}</p>}

      <div className="pt-2">
        <Button type="submit" isLoading={createMeal.isPending} className="w-full sm:w-auto">
          Log Meal
        </Button>
      </div>
    </form>
  );
}
