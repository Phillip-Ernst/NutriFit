import { describe, it, expect } from 'vitest';
import {
  getProfile,
  updateProfile,
  createMeasurement,
  getMeasurements,
  getLatestMeasurement,
  deleteMeasurement,
} from './profile';

describe('profile API', () => {
  describe('getProfile', () => {
    it('returns profile data', async () => {
      const profile = await getProfile();

      expect(profile).toHaveProperty('id');
      expect(profile).toHaveProperty('username');
      expect(profile).toHaveProperty('birthYear');
      expect(profile).toHaveProperty('age');
      expect(profile).toHaveProperty('gender');
      expect(profile).toHaveProperty('unitPreference');
      expect(profile).toHaveProperty('createdAt');
      expect(profile).toHaveProperty('updatedAt');
    });

    it('returns valid unit preference', async () => {
      const profile = await getProfile();

      expect(['IMPERIAL', 'METRIC']).toContain(profile.unitPreference);
    });
  });

  describe('updateProfile', () => {
    it('updates birth year', async () => {
      const updated = await updateProfile({ birthYear: 1995 });

      expect(updated.birthYear).toBe(1995);
    });

    it('updates gender', async () => {
      const updated = await updateProfile({ gender: 'FEMALE' });

      expect(updated.gender).toBe('FEMALE');
    });

    it('updates unit preference', async () => {
      const updated = await updateProfile({ unitPreference: 'METRIC' });

      expect(updated.unitPreference).toBe('METRIC');
    });
  });
});

describe('measurements API', () => {
  describe('getMeasurements', () => {
    it('returns array of measurements', async () => {
      const measurements = await getMeasurements();

      expect(Array.isArray(measurements)).toBe(true);
      expect(measurements.length).toBeGreaterThan(0);
    });

    it('returns measurements with correct structure', async () => {
      const measurements = await getMeasurements();
      const measurement = measurements[0];

      expect(measurement).toHaveProperty('id');
      expect(measurement).toHaveProperty('recordedAt');
      expect(measurement).toHaveProperty('heightCm');
      expect(measurement).toHaveProperty('weightKg');
      expect(measurement).toHaveProperty('bodyFatPercent');
      expect(measurement).toHaveProperty('notes');
    });

    it('returns measurements ordered by date (newest first)', async () => {
      const measurements = await getMeasurements();

      if (measurements.length >= 2) {
        const first = new Date(measurements[0].recordedAt);
        const second = new Date(measurements[1].recordedAt);
        expect(first.getTime()).toBeGreaterThanOrEqual(second.getTime());
      }
    });
  });

  describe('getLatestMeasurement', () => {
    it('returns the most recent measurement', async () => {
      const latest = await getLatestMeasurement();

      expect(latest).not.toBeNull();
      expect(latest).toHaveProperty('id');
      expect(latest).toHaveProperty('recordedAt');
    });

    it('returns measurement with body stats', async () => {
      const latest = await getLatestMeasurement();

      expect(latest).toHaveProperty('heightCm');
      expect(latest).toHaveProperty('weightKg');
      expect(latest).toHaveProperty('bodyFatPercent');
    });
  });

  describe('createMeasurement', () => {
    it('creates a measurement and returns response', async () => {
      const request = {
        heightCm: 175,
        weightKg: 75,
        bodyFatPercent: 18,
      };

      const response = await createMeasurement(request);

      expect(response).toHaveProperty('id');
      expect(response).toHaveProperty('recordedAt');
      expect(response.heightCm).toBe(175);
      expect(response.weightKg).toBe(75);
      expect(response.bodyFatPercent).toBe(18);
    });

    it('creates a measurement with body measurements', async () => {
      const request = {
        heightCm: 180,
        weightKg: 85,
        chestCm: 105,
        bicepsCm: 38,
        waistCm: 82,
      };

      const response = await createMeasurement(request);

      expect(response.chestCm).toBe(105);
      expect(response.bicepsCm).toBe(38);
      expect(response.waistCm).toBe(82);
    });

    it('handles partial measurements', async () => {
      const request = {
        weightKg: 80,
      };

      const response = await createMeasurement(request);

      expect(response.weightKg).toBe(80);
      expect(response.heightCm).toBeNull();
      expect(response.bodyFatPercent).toBeNull();
    });

    it('stores notes', async () => {
      const request = {
        weightKg: 78,
        notes: 'Morning weigh-in after fasting',
      };

      const response = await createMeasurement(request);

      expect(response.notes).toBe('Morning weigh-in after fasting');
    });

    it('returns ISO timestamp in recordedAt', async () => {
      const request = {
        weightKg: 80,
      };

      const response = await createMeasurement(request);

      expect(() => new Date(response.recordedAt)).not.toThrow();
      expect(response.recordedAt).toMatch(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/);
    });
  });

  describe('deleteMeasurement', () => {
    it('deletes a measurement without error', async () => {
      await expect(deleteMeasurement(1)).resolves.not.toThrow();
    });
  });
});
