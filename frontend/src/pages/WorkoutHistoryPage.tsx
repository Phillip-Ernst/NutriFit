import { useState } from 'react';
import { useMyWorkouts, useDeleteWorkout } from '../hooks/useWorkouts';
import WorkoutTable from '../components/workouts/WorkoutTable';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import ConfirmModal from '../components/ui/ConfirmModal';

export default function WorkoutHistoryPage() {
  const { data: workouts, isLoading } = useMyWorkouts();
  const deleteWorkout = useDeleteWorkout();
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);

  const handleDeleteClick = (id: number) => {
    setDeleteConfirm(id);
  };

  const handleConfirmDelete = () => {
    if (deleteConfirm !== null) {
      deleteWorkout.mutate(deleteConfirm, {
        onSuccess: () => setDeleteConfirm(null),
      });
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Workout History</h1>
        <p className="text-gray-400 mt-1">
          {workouts ? `${workouts.length} workout${workouts.length !== 1 ? 's' : ''} logged` : 'Loading...'}
        </p>
      </div>
      {isLoading ? (
        <LoadingSpinner />
      ) : (
        <WorkoutTable
          workouts={workouts || []}
          onDelete={handleDeleteClick}
          deletingId={deleteWorkout.isPending ? deleteConfirm : null}
        />
      )}

      <ConfirmModal
        isOpen={deleteConfirm !== null}
        onClose={() => setDeleteConfirm(null)}
        onConfirm={handleConfirmDelete}
        title="Delete Workout"
        message="Are you sure you want to delete this workout? This action cannot be undone."
        isLoading={deleteWorkout.isPending}
      />
    </div>
  );
}
