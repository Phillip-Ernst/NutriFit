import { useState, useMemo } from 'react';
import { usePredefinedExercises, useExerciseCategories } from '../../hooks/useWorkoutPlans';
import type { ExerciseCategory, PredefinedExercise, WorkoutPlanExercise } from '../../types';
import Modal from '../ui/Modal';
import LoadingSpinner from '../ui/LoadingSpinner';

interface ExercisePickerProps {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (exercise: WorkoutPlanExercise) => void;
}

export default function ExercisePicker({ isOpen, onClose, onSelect }: ExercisePickerProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<ExerciseCategory | undefined>(undefined);

  const { data: categories, isLoading: categoriesLoading } = useExerciseCategories();
  const { data: exercises, isLoading: exercisesLoading } = usePredefinedExercises(selectedCategory);

  const filteredExercises = useMemo(() => {
    if (!exercises) return [];
    if (!searchQuery.trim()) return exercises;

    const query = searchQuery.toLowerCase().trim();
    return exercises.filter((ex) =>
      ex.name.toLowerCase().includes(query)
    );
  }, [exercises, searchQuery]);

  const hasExactMatch = useMemo(() => {
    if (!searchQuery.trim()) return true;
    const query = searchQuery.toLowerCase().trim();
    return exercises?.some((ex) => ex.name.toLowerCase() === query) ?? false;
  }, [exercises, searchQuery]);

  const handleSelectPredefined = (exercise: PredefinedExercise) => {
    onSelect({
      name: exercise.name,
      category: exercise.category,
      isCustom: false,
      targetSets: null,
      targetReps: null,
      targetWeight: null,
    });
    resetAndClose();
  };

  const handleAddCustom = () => {
    if (!searchQuery.trim()) return;
    onSelect({
      name: searchQuery.trim(),
      category: selectedCategory ?? null,
      isCustom: true,
      targetSets: null,
      targetReps: null,
      targetWeight: null,
    });
    resetAndClose();
  };

  const resetAndClose = () => {
    setSearchQuery('');
    setSelectedCategory(undefined);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={resetAndClose} title="Add Exercise">
      <div className="space-y-4">
        <input
          type="text"
          placeholder="Search or type custom exercise..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          autoFocus
          className="w-full bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-3 py-2 text-sm outline-none"
        />

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
          ) : (
            <>
              {filteredExercises.map((exercise) => (
                <button
                  type="button"
                  key={exercise.id}
                  onClick={() => handleSelectPredefined(exercise)}
                  className="w-full text-left px-3 py-2 rounded-lg hover:bg-gray-800 transition-colors"
                >
                  <span className="text-white text-sm">{exercise.name}</span>
                  <span className="text-xs text-gray-500 ml-2">{exercise.category}</span>
                </button>
              ))}

              {searchQuery.trim() && !hasExactMatch && (
                <button
                  type="button"
                  onClick={handleAddCustom}
                  className="w-full text-left px-3 py-2 rounded-lg bg-emerald-900/30 hover:bg-emerald-900/50 border border-emerald-700/50 transition-colors"
                >
                  <span className="text-emerald-400 text-sm">
                    + Add "{searchQuery.trim()}" as custom exercise
                  </span>
                </button>
              )}

              {filteredExercises.length === 0 && !searchQuery.trim() && (
                <p className="text-gray-500 text-sm text-center py-4">No exercises found</p>
              )}
            </>
          )}
        </div>
      </div>
    </Modal>
  );
}
