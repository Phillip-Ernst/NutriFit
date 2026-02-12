import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useMyMeals, useDeleteMeal } from '../hooks/useMeals';
import MealTable from '../components/meals/MealTable';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import ConfirmModal from '../components/ui/ConfirmModal';

export default function MealHistoryPage() {
  const { data: meals, isLoading } = useMyMeals();
  const deleteMeal = useDeleteMeal();
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);

  const handleDeleteClick = (id: number) => {
    setDeleteConfirm(id);
  };

  const handleConfirmDelete = () => {
    if (deleteConfirm !== null) {
      deleteMeal.mutate(deleteConfirm, {
        onSuccess: () => setDeleteConfirm(null),
      });
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Meal History</h1>
        <p className="text-gray-400 mt-1">
          {meals ? `${meals.length} meal${meals.length !== 1 ? 's' : ''} logged` : 'Loading...'}
        </p>
      </div>

      <div className="flex flex-wrap gap-3">
        <Link
          to="/meals/log"
          className="inline-flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2 rounded-lg font-medium transition-colors"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Log Meal
        </Link>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : (
        <MealTable
          meals={meals || []}
          onDelete={handleDeleteClick}
          deletingId={deleteMeal.isPending ? deleteConfirm : null}
        />
      )}

      <ConfirmModal
        isOpen={deleteConfirm !== null}
        onClose={() => setDeleteConfirm(null)}
        onConfirm={handleConfirmDelete}
        title="Delete Meal"
        message="Are you sure you want to delete this meal? This action cannot be undone."
        isLoading={deleteMeal.isPending}
      />
    </div>
  );
}
