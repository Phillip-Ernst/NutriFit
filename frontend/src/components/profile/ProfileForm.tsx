import { useState, useEffect } from 'react';
import type { ProfileResponse, ProfileUpdateRequest, Gender, UnitPreference } from '../../types';
import Button from '../ui/Button';
import Input from '../ui/Input';

interface ProfileFormProps {
  profile: ProfileResponse;
  onSubmit: (data: ProfileUpdateRequest) => void;
  isSubmitting: boolean;
  onCancel: () => void;
}

const GENDER_OPTIONS: { value: Gender; label: string }[] = [
  { value: 'MALE', label: 'Male' },
  { value: 'FEMALE', label: 'Female' },
  { value: 'OTHER', label: 'Other' },
  { value: 'PREFER_NOT_TO_SAY', label: 'Prefer not to say' },
];

const UNIT_OPTIONS: { value: UnitPreference; label: string }[] = [
  { value: 'IMPERIAL', label: 'Imperial (lb, in)' },
  { value: 'METRIC', label: 'Metric (kg, cm)' },
];

export default function ProfileForm({ profile, onSubmit, isSubmitting, onCancel }: ProfileFormProps) {
  const [birthYear, setBirthYear] = useState<string>(profile.birthYear?.toString() ?? '');
  const [gender, setGender] = useState<Gender | ''>(profile.gender ?? '');
  const [unitPreference, setUnitPreference] = useState<UnitPreference>(profile.unitPreference);

  useEffect(() => {
    setBirthYear(profile.birthYear?.toString() ?? '');
    setGender(profile.gender ?? '');
    setUnitPreference(profile.unitPreference);
  }, [profile]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const data: ProfileUpdateRequest = {
      birthYear: birthYear ? parseInt(birthYear, 10) : null,
      gender: gender || null,
      unitPreference,
    };
    onSubmit(data);
  };

  const currentYear = new Date().getFullYear();
  const minYear = currentYear - 120;

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <Input
        type="number"
        label="Birth Year"
        name="birthYear"
        value={birthYear}
        onChange={(e) => setBirthYear(e.target.value)}
        placeholder="e.g., 1990"
        min={minYear}
        max={currentYear}
      />

      <div className="flex flex-col gap-1">
        <label htmlFor="gender" className="text-sm font-medium text-gray-300">
          Gender
        </label>
        <select
          id="gender"
          value={gender}
          onChange={(e) => setGender(e.target.value as Gender | '')}
          className="bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-4 py-2 outline-none transition-colors"
        >
          <option value="">Select gender</option>
          {GENDER_OPTIONS.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="unitPreference" className="text-sm font-medium text-gray-300">
          Unit Preference
        </label>
        <select
          id="unitPreference"
          value={unitPreference}
          onChange={(e) => setUnitPreference(e.target.value as UnitPreference)}
          className="bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-4 py-2 outline-none transition-colors"
        >
          {UNIT_OPTIONS.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      </div>

      <div className="flex gap-3 pt-2">
        <Button type="submit" isLoading={isSubmitting}>
          Save
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
      </div>
    </form>
  );
}
