import type { FoodItem } from '../../types';

interface FoodItemRowProps {
  index: number;
  food: FoodItem;
  onChange: (index: number, field: keyof FoodItem, value: string) => void;
  onRemove: (index: number) => void;
  canRemove: boolean;
}

export default function FoodItemRow({ index, food, onChange, onRemove, canRemove }: FoodItemRowProps) {
  return (
    <div className="flex flex-col sm:flex-row gap-2 p-3 bg-gray-800 rounded-lg">
      <input
        type="text"
        placeholder="Food name"
        value={food.type}
        onChange={(e) => onChange(index, 'type', e.target.value)}
        required
        className="flex-1 bg-gray-700 border border-gray-600 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-md px-3 py-2 text-sm outline-none placeholder:text-gray-500"
      />
      <div className="flex gap-2">
        <input
          type="number"
          placeholder="Cals"
          value={food.calories ?? ''}
          onChange={(e) => onChange(index, 'calories', e.target.value)}
          min="0"
          className="w-20 bg-gray-700 border border-gray-600 focus:border-orange-400 focus:ring-1 focus:ring-orange-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="number"
          placeholder="Protein"
          value={food.protein ?? ''}
          onChange={(e) => onChange(index, 'protein', e.target.value)}
          min="0"
          className="w-20 bg-gray-700 border border-gray-600 focus:border-blue-400 focus:ring-1 focus:ring-blue-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="number"
          placeholder="Carbs"
          value={food.carbs ?? ''}
          onChange={(e) => onChange(index, 'carbs', e.target.value)}
          min="0"
          className="w-20 bg-gray-700 border border-gray-600 focus:border-yellow-400 focus:ring-1 focus:ring-yellow-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
        <input
          type="number"
          placeholder="Fats"
          value={food.fats ?? ''}
          onChange={(e) => onChange(index, 'fats', e.target.value)}
          min="0"
          className="w-20 bg-gray-700 border border-gray-600 focus:border-pink-400 focus:ring-1 focus:ring-pink-400 text-white rounded-md px-2 py-2 text-sm outline-none placeholder:text-gray-500"
        />
      </div>
      {canRemove && (
        <button
          type="button"
          onClick={() => onRemove(index)}
          className="text-red-400 hover:text-red-300 transition-colors self-center px-2"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
          </svg>
        </button>
      )}
    </div>
  );
}
