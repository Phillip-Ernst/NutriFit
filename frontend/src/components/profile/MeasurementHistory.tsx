import type { MeasurementResponse, UnitPreference } from '../../types';
import MeasurementCard from './MeasurementCard';

interface MeasurementHistoryProps {
  measurements: MeasurementResponse[];
  unitPreference: UnitPreference;
  onDelete: (id: number) => void;
  deletingId: number | null;
}

export default function MeasurementHistory({
  measurements,
  unitPreference,
  onDelete,
  deletingId,
}: MeasurementHistoryProps) {
  if (measurements.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-gray-400">No measurements recorded yet.</p>
        <p className="text-sm text-gray-500 mt-1">
          Start tracking your progress by logging your first measurement.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {measurements.map((measurement) => (
        <MeasurementCard
          key={measurement.id}
          measurement={measurement}
          unitPreference={unitPreference}
          onDelete={onDelete}
          isDeleting={deletingId === measurement.id}
        />
      ))}
    </div>
  );
}
