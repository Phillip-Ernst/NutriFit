import { useState } from 'react';
import { usePredefinedExercises, useExerciseCategories } from '../../hooks/useWorkoutPlans';
import type { ExerciseCategory, PredefinedExercise, WorkoutPlanExercise } from '../../types';
import Modal from '../ui/Modal';
import Button from '../ui/Button';
import LoadingSpinner from '../ui/LoadingSpinner';

interface ExercisePickerProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (exercise: WorkoutPlanExercise) => void;
}

export default function ExercisePicker({ isOpen, onClose, onSelect }: ExercisePickerProps) {
  const [selectedCategory, setSelectedCategory] = useState<ExerciseCategory | undefined>(undefined);
  const [customName, setCustomName] = useState('');
  const [isCustomMode, setIsCustomMode] = useState(false);

  const { data: categories, isLoading: categoriesLoading } = useExerciseCategories();
  const { data: exercises, isLoading: exercisesLoading } = usePredefinedExercises(selectedCategory);

  const handleSelectPredefined = (exercise: PredefinedExercise) => {
    onSelect({
      name: exercise.name,
      category: exercise.category,
      isCustom: false,
      targetSets: null,
      targetReps: null,
      targetWeight: null,
    });
    onClose();
  };

  const handleAddCustom = () => {
    if (!customName.trim()) return;
    onSelect({
      name: customName.trim(),
      category: selectedCategory ?? null,
      isCustom: true,
      targetSets: null,
      targetReps: null,
      targetWeight: null,
    });
    setCustomName('');
    setIsCustomMode(false);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Add Exercise">
      <div className="space-y-4">
        <div className="flex gap-2">
          <button
            onClick={() => setIsCustomMode(false)}
            className={`flex-1 py-2 px-3 rounded-lg text-sm font-medium transition-colors ${
              !isCustomMode
                ? 'bg-emerald-600 text-white'
                : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
            }`}
          >
            Predefined
          </button>
          <button
            onClick={() => setIsCustomMode(true)}
            className={`flex-1 py-2 px-3 rounded-lg text-sm font-medium transition-colors ${
              isCustomMode
                ? 'bg-emerald-600 text-white'
                : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
            }`}
          >
            Custom
          </button>
        </div>

        {isCustomMode ? (
          <div className="space-y-3">
            <input
              type="text"
              placeholder="Exercise name"
              value={customName}
              onChange={(e) => setCustomName(e.target.value)}
              className="w-full bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-3 py-2 text-sm outline-none"
            />
            <select
              value={selectedCategory ?? ''}
              onChange={(e) => setSelectedCategory(e.target.value as ExerciseCategory || undefined)}
              className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-3 py-2 text-sm outline-none"
            >
              <option value="">Select category (optional)</option>
              {categories?.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
            <Button onClick={handleAddCustom} disabled={!customName.trim()} className="w-full">
              Add Custom Exercise
            </Button>
          </div>
        ) : (
          <div className="space-y-3">
            {categoriesLoading ? (
              <LoadingSpinner />
            ) : (
              <select
                value={selectedCategory ?? ''}
                onChange={(e) => setSelectedCategory(e.target.value as ExerciseCategory || undefined)}
                className="w-full bg-gray-800 border border-gray-700 text-white rounded-lg px-3 py-2 text-sm outline-none"
              >
                <option value="">All Categories</option>
                {categories?.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat}
                  </option>
                ))}
              </select>
            )}

            <div className="max-h-64 overflow-y-auto space-y-1">
              {exercisesLoading ? (
                <LoadingSpinner />
              ) : exercises?.length === 0 ? (
                <p className="text-gray-500 text-sm text-center py-4">No exercises found</p>
              ) : (
                exercises?.map((exercise) => (
                  <button
                    key={exercise.id}
                    onClick={() => handleSelectPredefined(exercise)}
                    className="w-full text-left px-3 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                  >
                    <span className="text-white text-sm">{exercise.name}</span>
                    <span className="text-xs text-gray-500 ml-2">{exercise.category}</span>
                  </button>
                ))
              )}
            </div>
          </div>
        )}
      </div>
    </Modal>
  );
}
