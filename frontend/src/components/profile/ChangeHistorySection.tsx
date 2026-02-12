import type { UserChangeHistoryResponse } from '../../types';
import Card from '../ui/Card';

// Format field names to be human-readable
function formatFieldName(fieldName: string): string {
  const fieldMap: Record<string, string> = {
    birthYear: 'Birth Year',
    gender: 'Gender',
    unitPreference: 'Unit Preference',
    heightCm: 'Height',
    weightKg: 'Weight',
    bodyFatPercent: 'Body Fat',
    neckCm: 'Neck',
    shouldersCm: 'Shoulders',
    chestCm: 'Chest',
    bicepsCm: 'Biceps',
    forearmsCm: 'Forearms',
    waistCm: 'Waist',
    hipsCm: 'Hips',
    thighsCm: 'Thighs',
    calvesCm: 'Calves',
  };
  return fieldMap[fieldName] || fieldName;
}

// Format values for display
function formatValue(fieldName: string, value: string | null): string {
  if (value === null) return '-';

  // Gender formatting
  if (fieldName === 'gender') {
    const genderMap: Record<string, string> = {
      MALE: 'Male',
      FEMALE: 'Female',
      OTHER: 'Other',
      PREFER_NOT_TO_SAY: 'Prefer not to say',
    };
    return genderMap[value] || value;
  }

  // Unit preference formatting
  if (fieldName === 'unitPreference') {
    return value === 'IMPERIAL' ? 'Imperial' : 'Metric';
  }

  return value;
}

// Format date for display
function formatDate(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  });
}

// Group changes by date
function groupByDate(changes: UserChangeHistoryResponse[]): Map<string, UserChangeHistoryResponse[]> {
  const groups = new Map<string, UserChangeHistoryResponse[]>();

  for (const change of changes) {
    const date = new Date(change.changedAt).toLocaleDateString();
    const existing = groups.get(date) || [];
    existing.push(change);
    groups.set(date, existing);
  }

  return groups;
}

interface ChangeHistorySectionProps {
  changes: UserChangeHistoryResponse[];
}

export default function ChangeHistorySection({ changes }: ChangeHistorySectionProps) {
  if (changes.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-gray-400">No changes recorded yet.</p>
      </div>
    );
  }

  const grouped = groupByDate(changes);

  return (
    <div className="space-y-4">
      {Array.from(grouped.entries()).map(([date, dateChanges]) => (
        <Card key={date} className="space-y-3">
          <p className="text-sm font-medium text-gray-400">{date}</p>
          <div className="space-y-2">
            {dateChanges.map((change) => (
              <div key={change.id} className="text-sm border-l-2 border-gray-700 pl-3 py-1">
                <div className="flex items-center gap-2">
                  <span className={`text-xs px-2 py-0.5 rounded ${
                    change.entityType === 'PROFILE'
                      ? 'bg-blue-900/50 text-blue-400'
                      : 'bg-purple-900/50 text-purple-400'
                  }`}>
                    {change.entityType === 'PROFILE' ? 'Profile' : 'Measurement'}
                  </span>
                  <span className="text-gray-300 font-medium">
                    {formatFieldName(change.fieldName)}
                  </span>
                  <span className="text-xs text-gray-500">
                    {formatDate(change.changedAt)}
                  </span>
                </div>
                <div className="mt-1 text-gray-400">
                  {change.oldValue === null ? (
                    <span>
                      Set to <span className="text-emerald-400">{formatValue(change.fieldName, change.newValue)}</span>
                    </span>
                  ) : change.newValue === null ? (
                    <span>
                      Removed <span className="text-red-400">{formatValue(change.fieldName, change.oldValue)}</span>
                    </span>
                  ) : (
                    <span>
                      Changed from{' '}
                      <span className="text-gray-300">{formatValue(change.fieldName, change.oldValue)}</span>
                      {' '}to{' '}
                      <span className="text-emerald-400">{formatValue(change.fieldName, change.newValue)}</span>
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </Card>
      ))}
    </div>
  );
}
