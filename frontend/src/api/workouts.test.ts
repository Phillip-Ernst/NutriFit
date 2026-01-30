import { describe, it, expect } from 'vitest';
import { createWorkout, getMyWorkouts } from './workouts';

describe('workouts API', () => {
  describe('getMyWorkouts', () => {
    it('returns array of workouts', async () => {
      const workouts = await getMyWorkouts();

      expect(Array.isArray(workouts)).toBe(true);
      expect(workouts.length).toBeGreaterThan(0);
      expect(workouts[0]).toHaveProperty('id');
      expect(workouts[0]).toHaveProperty('createdAt');
      expect(workouts[0]).toHaveProperty('exercises');
    });

    it('returns workout with correct structure', async () => {
      const workouts = await getMyWorkouts();
      const workout = workouts[0];

      expect(workout).toHaveProperty('totalDurationMinutes');
      expect(workout).toHaveProperty('totalCaloriesBurned');
      expect(workout).toHaveProperty('totalSets');
      expect(workout).toHaveProperty('totalReps');
      expect(typeof workout.totalDurationMinutes).toBe('number');
    });
  });

  describe('createWorkout', () => {
    it('creates a workout and returns response', async () => {
      const request = {
        exercises: [
          {
            name: 'Push-ups',
            category: 'Chest',
            sets: 3,
            reps: 15,
            weight: null,
            durationMinutes: null,
            caloriesBurned: null,
          },
        ],
      };

      const response = await createWorkout(request);

      expect(response).toHaveProperty('id');
      expect(response).toHaveProperty('createdAt');
      expect(response.exercises).toHaveLength(1);
      expect(response.exercises[0].name).toBe('Push-ups');
    });

    it('calculates totals correctly', async () => {
      const request = {
        exercises: [
          {
            name: 'Bench Press',
            category: 'Chest',
            sets: 4,
            reps: 10,
            weight: 135,
            durationMinutes: 15,
            caloriesBurned: 100,
          },
          {
            name: 'Running',
            category: 'Cardio',
            sets: null,
            reps: null,
            weight: null,
            durationMinutes: 30,
            caloriesBurned: 300,
          },
        ],
      };

      const response = await createWorkout(request);

      expect(response.totalSets).toBe(4);
      expect(response.totalReps).toBe(10);
      expect(response.totalDurationMinutes).toBe(45);
      expect(response.totalCaloriesBurned).toBe(400);
    });
  });
});
