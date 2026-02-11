import { useState } from 'react';
import type { MeasurementRequest, UnitPreference } from '../../types';
import Button from '../ui/Button';
import Input from '../ui/Input';

interface MeasurementFormProps {
  unitPreference: UnitPreference;
  onSubmit: (data: MeasurementRequest) => void;
  isSubmitting: boolean;
  onCancel: () => void;
}

// Conversion helpers
const cmToIn = (cm: number): number => cm / 2.54;
const inToCm = (inches: number): number => inches * 2.54;
const kgToLb = (kg: number): number => kg * 2.20462;
const lbToKg = (lb: number): number => lb / 2.20462;

function parseNum(val: string): number | null {
  if (!val || val.trim() === '') return null;
  const num = parseFloat(val);
  return isNaN(num) ? null : num;
}

export default function MeasurementForm({
  unitPreference,
  onSubmit,
  isSubmitting,
  onCancel,
}: MeasurementFormProps) {
  const isImperial = unitPreference === 'IMPERIAL';
  const lengthUnit = isImperial ? 'in' : 'cm';
  const weightUnit = isImperial ? 'lb' : 'kg';

  // Form state (in display units)
  const [height, setHeight] = useState('');
  const [weight, setWeight] = useState('');
  const [bodyFat, setBodyFat] = useState('');
  const [neck, setNeck] = useState('');
  const [shoulders, setShoulders] = useState('');
  const [chest, setChest] = useState('');
  const [biceps, setBiceps] = useState('');
  const [forearms, setForearms] = useState('');
  const [waist, setWaist] = useState('');
  const [hips, setHips] = useState('');
  const [thighs, setThighs] = useState('');
  const [calves, setCalves] = useState('');
  const [notes, setNotes] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Convert to metric before sending
    const convertLength = (val: string) => {
      const num = parseNum(val);
      if (num === null) return null;
      return isImperial ? inToCm(num) : num;
    };

    const convertWeight = (val: string) => {
      const num = parseNum(val);
      if (num === null) return null;
      return isImperial ? lbToKg(num) : num;
    };

    const data: MeasurementRequest = {
      heightCm: convertLength(height),
      weightKg: convertWeight(weight),
      bodyFatPercent: parseNum(bodyFat),
      neckCm: convertLength(neck),
      shouldersCm: convertLength(shoulders),
      chestCm: convertLength(chest),
      bicepsCm: convertLength(biceps),
      forearmsCm: convertLength(forearms),
      waistCm: convertLength(waist),
      hipsCm: convertLength(hips),
      thighsCm: convertLength(thighs),
      calvesCm: convertLength(calves),
      notes: notes.trim() || null,
    };

    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="space-y-4">
        <h4 className="text-sm font-medium text-gray-400 uppercase tracking-wide">Core Stats</h4>
        <div className="grid grid-cols-2 gap-4">
          <Input
            type="number"
            step="0.1"
            label={`Height (${lengthUnit})`}
            name="height"
            value={height}
            onChange={(e) => setHeight(e.target.value)}
            placeholder={isImperial ? 'e.g., 70' : 'e.g., 178'}
          />
          <Input
            type="number"
            step="0.1"
            label={`Weight (${weightUnit})`}
            name="weight"
            value={weight}
            onChange={(e) => setWeight(e.target.value)}
            placeholder={isImperial ? 'e.g., 175' : 'e.g., 80'}
          />
        </div>
        <Input
          type="number"
          step="0.1"
          label="Body Fat (%)"
          name="bodyFat"
          value={bodyFat}
          onChange={(e) => setBodyFat(e.target.value)}
          placeholder="e.g., 15"
        />
      </div>

      <div className="space-y-4">
        <h4 className="text-sm font-medium text-gray-400 uppercase tracking-wide">Upper Body</h4>
        <div className="grid grid-cols-2 gap-4">
          <Input
            type="number"
            step="0.1"
            label={`Neck (${lengthUnit})`}
            name="neck"
            value={neck}
            onChange={(e) => setNeck(e.target.value)}
          />
          <Input
            type="number"
            step="0.1"
            label={`Shoulders (${lengthUnit})`}
            name="shoulders"
            value={shoulders}
            onChange={(e) => setShoulders(e.target.value)}
          />
          <Input
            type="number"
            step="0.1"
            label={`Chest (${lengthUnit})`}
            name="chest"
            value={chest}
            onChange={(e) => setChest(e.target.value)}
          />
          <Input
            type="number"
            step="0.1"
            label={`Biceps (${lengthUnit})`}
            name="biceps"
            value={biceps}
            onChange={(e) => setBiceps(e.target.value)}
          />
          <Input
            type="number"
            step="0.1"
            label={`Forearms (${lengthUnit})`}
            name="forearms"
            value={forearms}
            onChange={(e) => setForearms(e.target.value)}
          />
        </div>
      </div>

      <div className="space-y-4">
        <h4 className="text-sm font-medium text-gray-400 uppercase tracking-wide">Core</h4>
        <div className="grid grid-cols-2 gap-4">
          <Input
            type="number"
            step="0.1"
            label={`Waist (${lengthUnit})`}
            name="waist"
            value={waist}
            onChange={(e) => setWaist(e.target.value)}
          />
          <Input
            type="number"
            step="0.1"
            label={`Hips (${lengthUnit})`}
            name="hips"
            value={hips}
            onChange={(e) => setHips(e.target.value)}
          />
        </div>
      </div>

      <div className="space-y-4">
        <h4 className="text-sm font-medium text-gray-400 uppercase tracking-wide">Lower Body</h4>
        <div className="grid grid-cols-2 gap-4">
          <Input
            type="number"
            step="0.1"
            label={`Thighs (${lengthUnit})`}
            name="thighs"
            value={thighs}
            onChange={(e) => setThighs(e.target.value)}
          />
          <Input
            type="number"
            step="0.1"
            label={`Calves (${lengthUnit})`}
            name="calves"
            value={calves}
            onChange={(e) => setCalves(e.target.value)}
          />
        </div>
      </div>

      <div className="space-y-2">
        <label htmlFor="notes" className="text-sm font-medium text-gray-300">
          Notes (optional)
        </label>
        <textarea
          id="notes"
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          placeholder="Any notes about this measurement session..."
          maxLength={500}
          rows={3}
          className="w-full bg-gray-800 border border-gray-700 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 text-white rounded-lg px-4 py-2 outline-none transition-colors placeholder:text-gray-500 resize-none"
        />
      </div>

      <div className="flex gap-3 pt-2">
        <Button type="submit" isLoading={isSubmitting}>
          Save Measurement
        </Button>
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
      </div>
    </form>
  );
}
