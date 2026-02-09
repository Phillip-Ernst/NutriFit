import { useState } from 'react';
import type { MeasurementResponse, UnitPreference } from '../../types';
import Card from '../ui/Card';
import Button from '../ui/Button';

interface MeasurementCardProps {
  measurement: MeasurementResponse;
  unitPreference: UnitPreference;
  onDelete?: (id: number) => void;
  isDeleting?: boolean;
}

// Conversion helpers
const cmToIn = (cm: number): number => cm / 2.54;
const kgToLb = (kg: number): number => kg * 2.20462;

function formatDate(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, {
    weekday: 'short',
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  });
}

function formatValue(
  val: number | null,
  unit: string,
  isImperial: boolean,
  isWeight: boolean = false
): string {
  if (val === null) return '-';
  let displayVal = val;
  if (isImperial) {
    displayVal = isWeight ? kgToLb(val) : cmToIn(val);
  }
  return `${displayVal.toFixed(1)} ${unit}`;
}

export default function MeasurementCard({
  measurement,
  unitPreference,
  onDelete,
  isDeleting,
}: MeasurementCardProps) {
  const [expanded, setExpanded] = useState(false);
  const isImperial = unitPreference === 'IMPERIAL';
  const lengthUnit = isImperial ? 'in' : 'cm';
  const weightUnit = isImperial ? 'lb' : 'kg';

  const coreStats = [
    { label: 'Height', value: measurement.heightCm, isWeight: false },
    { label: 'Weight', value: measurement.weightKg, isWeight: true },
    { label: 'Body Fat', value: measurement.bodyFatPercent, isPercent: true },
  ];

  const upperBodyStats = [
    { label: 'Neck', value: measurement.neckCm },
    { label: 'Shoulders', value: measurement.shouldersCm },
    { label: 'Chest', value: measurement.chestCm },
    { label: 'Biceps', value: measurement.bicepsCm },
    { label: 'Forearms', value: measurement.forearmsCm },
  ];

  const coreBodyStats = [
    { label: 'Waist', value: measurement.waistCm },
    { label: 'Hips', value: measurement.hipsCm },
  ];

  const lowerBodyStats = [
    { label: 'Thighs', value: measurement.thighsCm },
    { label: 'Calves', value: measurement.calvesCm },
  ];

  const hasAnyMeasurement =
    [...upperBodyStats, ...coreBodyStats, ...lowerBodyStats].some((s) => s.value !== null);

  return (
    <Card className="space-y-3">
      <div className="flex items-start justify-between">
        <p className="text-sm text-gray-400">{formatDate(measurement.recordedAt)}</p>
        {onDelete && (
          <Button
            variant="danger"
            size="sm"
            onClick={() => onDelete(measurement.id)}
            isLoading={isDeleting}
          >
            Delete
          </Button>
        )}
      </div>

      <div className="flex flex-wrap gap-4 text-sm">
        {coreStats.map((stat) => {
          if (stat.value === null) return null;
          const display = stat.isPercent
            ? `${stat.value.toFixed(1)}%`
            : formatValue(stat.value, stat.isWeight ? weightUnit : lengthUnit, isImperial, stat.isWeight);
          return (
            <div key={stat.label}>
              <span className="text-gray-400">{stat.label}: </span>
              <span className="text-white font-medium">{display}</span>
            </div>
          );
        })}
      </div>

      {hasAnyMeasurement && (
        <>
          <button
            onClick={() => setExpanded(!expanded)}
            className="text-sm text-gray-400 hover:text-gray-200 transition-colors"
          >
            {expanded ? 'Hide' : 'Show'} body measurements
          </button>

          {expanded && (
            <div className="border-t border-gray-800 pt-3 space-y-4">
              {upperBodyStats.some((s) => s.value !== null) && (
                <div>
                  <p className="text-xs text-gray-500 uppercase tracking-wide mb-2">Upper Body</p>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    {upperBodyStats.map((stat) => (
                      <div key={stat.label}>
                        <span className="text-gray-400">{stat.label}: </span>
                        <span className="text-white">
                          {formatValue(stat.value, lengthUnit, isImperial)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {coreBodyStats.some((s) => s.value !== null) && (
                <div>
                  <p className="text-xs text-gray-500 uppercase tracking-wide mb-2">Core</p>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    {coreBodyStats.map((stat) => (
                      <div key={stat.label}>
                        <span className="text-gray-400">{stat.label}: </span>
                        <span className="text-white">
                          {formatValue(stat.value, lengthUnit, isImperial)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {lowerBodyStats.some((s) => s.value !== null) && (
                <div>
                  <p className="text-xs text-gray-500 uppercase tracking-wide mb-2">Lower Body</p>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    {lowerBodyStats.map((stat) => (
                      <div key={stat.label}>
                        <span className="text-gray-400">{stat.label}: </span>
                        <span className="text-white">
                          {formatValue(stat.value, lengthUnit, isImperial)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {measurement.notes && (
                <div>
                  <p className="text-xs text-gray-500 uppercase tracking-wide mb-2">Notes</p>
                  <p className="text-sm text-gray-300">{measurement.notes}</p>
                </div>
              )}
            </div>
          )}
        </>
      )}
    </Card>
  );
}
