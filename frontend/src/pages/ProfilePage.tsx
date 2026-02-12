import { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { useMyMeals } from '../hooks/useMeals';
import {
  useProfile,
  useUpdateProfile,
  useMeasurements,
  useLatestMeasurement,
  useCreateMeasurement,
  useDeleteMeasurement,
  useChangeHistory,
} from '../hooks/useProfile';
import Card from '../components/ui/Card';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import LoadingSpinner from '../components/ui/LoadingSpinner';
import { ProfileForm, MeasurementForm, MeasurementHistory, ChangeHistorySection } from '../components/profile';
import type { ProfileUpdateRequest, MeasurementRequest, UnitPreference } from '../types';

// Conversion helpers for display
const cmToIn = (cm: number): number => cm / 2.54;
const kgToLb = (kg: number): number => kg * 2.20462;

function formatHeight(cm: number | null, isImperial: boolean): string {
  if (cm === null) return '-';
  if (isImperial) {
    const totalInches = cmToIn(cm);
    const feet = Math.floor(totalInches / 12);
    const inches = Math.round(totalInches % 12);
    return `${feet}'${inches}"`;
  }
  return `${cm.toFixed(0)} cm`;
}

function formatWeight(kg: number | null, isImperial: boolean): string {
  if (kg === null) return '-';
  if (isImperial) {
    return `${kgToLb(kg).toFixed(1)} lb`;
  }
  return `${kg.toFixed(1)} kg`;
}

function formatGender(gender: string | null): string {
  if (!gender) return '-';
  const map: Record<string, string> = {
    MALE: 'Male',
    FEMALE: 'Female',
    OTHER: 'Other',
    PREFER_NOT_TO_SAY: 'Prefer not to say',
  };
  return map[gender] || gender;
}

function formatDate(iso: string): string {
  const d = new Date(iso);
  return d.toLocaleDateString(undefined, {
    month: 'short',
    day: 'numeric',
  });
}

export default function ProfilePage() {
  const { username, logout } = useAuth();
  const { data: meals } = useMyMeals();
  const { data: profile, isLoading: profileLoading } = useProfile();
  const { data: measurements, isLoading: measurementsLoading } = useMeasurements();
  const { data: latestMeasurement } = useLatestMeasurement();
  const { data: changeHistory, isLoading: historyLoading } = useChangeHistory();

  const updateProfile = useUpdateProfile();
  const createMeasurement = useCreateMeasurement();
  const deleteMeasurement = useDeleteMeasurement();

  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [isAddingMeasurement, setIsAddingMeasurement] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const handleProfileSubmit = (data: ProfileUpdateRequest) => {
    updateProfile.mutate(data, {
      onSuccess: () => setIsEditingProfile(false),
    });
  };

  const handleMeasurementSubmit = (data: MeasurementRequest) => {
    createMeasurement.mutate(data, {
      onSuccess: () => setIsAddingMeasurement(false),
    });
  };

  const handleDeleteMeasurement = (id: number) => {
    setDeletingId(id);
    deleteMeasurement.mutate(id, {
      onSettled: () => setDeletingId(null),
    });
  };

  if (profileLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <LoadingSpinner />
      </div>
    );
  }

  const isImperial = profile?.unitPreference === 'IMPERIAL';
  const unitPreference: UnitPreference = profile?.unitPreference ?? 'IMPERIAL';

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-white">Profile</h1>

      {/* User Info Section */}
      <Card className="space-y-4">
        <div className="flex items-center gap-4">
          <div className="w-14 h-14 rounded-full bg-emerald-500/20 flex items-center justify-center">
            <span className="text-2xl font-bold text-emerald-400">
              {username?.charAt(0).toUpperCase()}
            </span>
          </div>
          <div>
            <p className="text-lg font-semibold text-white">{username}</p>
            <p className="text-sm text-gray-400">
              {meals ? `${meals.length} meal${meals.length !== 1 ? 's' : ''} logged` : ''}
            </p>
          </div>
        </div>
      </Card>

      {/* Profile Information Section */}
      <Card className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-white">Profile Information</h2>
          {!isEditingProfile && (
            <Button variant="secondary" size="sm" onClick={() => setIsEditingProfile(true)}>
              Edit
            </Button>
          )}
        </div>

        {isEditingProfile && profile ? (
          <ProfileForm
            profile={profile}
            onSubmit={handleProfileSubmit}
            isSubmitting={updateProfile.isPending}
            onCancel={() => setIsEditingProfile(false)}
          />
        ) : (
          <div className="flex flex-wrap gap-6 text-sm">
            <div>
              <span className="text-gray-400">Age: </span>
              <span className="text-white">{profile?.age ?? '-'}</span>
            </div>
            <div>
              <span className="text-gray-400">Gender: </span>
              <span className="text-white">{formatGender(profile?.gender ?? null)}</span>
            </div>
            <div>
              <span className="text-gray-400">Units: </span>
              <span className="text-white">{isImperial ? 'Imperial' : 'Metric'}</span>
            </div>
          </div>
        )}
      </Card>

      {/* Latest Stats Section */}
      {latestMeasurement && (
        <Card className="space-y-4">
          <h2 className="text-lg font-semibold text-white">
            Latest Stats{' '}
            <span className="text-sm font-normal text-gray-400">
              (from {formatDate(latestMeasurement.recordedAt)})
            </span>
          </h2>
          <div className="flex flex-wrap gap-6 text-sm">
            <div>
              <span className="text-gray-400">Height: </span>
              <span className="text-white">
                {formatHeight(latestMeasurement.heightCm, isImperial)}
              </span>
            </div>
            <div>
              <span className="text-gray-400">Weight: </span>
              <span className="text-white">
                {formatWeight(latestMeasurement.weightKg, isImperial)}
              </span>
            </div>
            {latestMeasurement.bodyFatPercent !== null && (
              <div>
                <span className="text-gray-400">Body Fat: </span>
                <span className="text-white">{latestMeasurement.bodyFatPercent.toFixed(1)}%</span>
              </div>
            )}
          </div>
        </Card>
      )}

      {/* Log New Measurement Button */}
      <Card className="flex items-center justify-between">
        <span className="text-white font-medium">Log New Measurement</span>
        <Button onClick={() => setIsAddingMeasurement(true)}>+</Button>
      </Card>

      {/* Measurement History Section */}
      <div className="space-y-4">
        <h2 className="text-lg font-semibold text-white">Measurement History</h2>
        {measurementsLoading ? (
          <div className="flex items-center justify-center py-8">
            <LoadingSpinner />
          </div>
        ) : (
          <MeasurementHistory
            measurements={measurements ?? []}
            unitPreference={unitPreference}
            onDelete={handleDeleteMeasurement}
            deletingId={deletingId}
          />
        )}
      </div>

      {/* Change History Section */}
      <div className="space-y-4">
        <h2 className="text-lg font-semibold text-white">Change History</h2>
        {historyLoading ? (
          <div className="flex items-center justify-center py-8">
            <LoadingSpinner />
          </div>
        ) : (
          <ChangeHistorySection changes={changeHistory ?? []} />
        )}
      </div>

      {/* Logout Section */}
      <Card>
        <Button variant="danger" onClick={logout}>
          Logout
        </Button>
      </Card>

      {/* Add Measurement Modal */}
      <Modal
        isOpen={isAddingMeasurement}
        onClose={() => setIsAddingMeasurement(false)}
        title="Log New Measurement"
      >
        <MeasurementForm
          unitPreference={unitPreference}
          onSubmit={handleMeasurementSubmit}
          isSubmitting={createMeasurement.isPending}
          onCancel={() => setIsAddingMeasurement(false)}
        />
      </Modal>
    </div>
  );
}
