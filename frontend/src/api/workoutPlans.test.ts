import { describe, it, expect } from 'vitest';
import {
  createWorkoutPlan,
  getMyWorkoutPlans,
  getWorkoutPlan,
  getPredefinedExercises,
  getExerciseCategories,
  getWorkoutPlanDay,
  createWorkoutFromPlan,
} from './workoutPlans';

describe('workoutPlans API', () => {
  describe('getMyWorkoutPlans', () => {
    it('should return array of workout plans', async () => {
      const plans = await getMyWorkoutPlans();

      expect(Array.isArray(plans)).toBe(true);
      expect(plans.length).toBeGreaterThan(0);
      expect(plans[0]).toHaveProperty('id');
      expect(plans[0]).toHaveProperty('name');
      expect(plans[0]).toHaveProperty('days');
    });
  });

  describe('getWorkoutPlan', () => {
    it('should return a single workout plan', async () => {
      const plan = await getWorkoutPlan(1);

      expect(plan.id).toBe(1);
      expect(plan.name).toBe('PPL Split');
      expect(plan.days.length).toBe(3);
    });
  });

  describe('createWorkoutPlan', () => {
    it('should create and return a new workout plan', async () => {
      const newPlan = await createWorkoutPlan({
        name: 'New Plan',
        description: 'Test description',
        days: [
          {
            dayNumber: 1,
            dayName: 'Day One',
            exercises: [
              {
                name: 'Squats',
                category: 'QUADS',
                isCustom: false,
                targetSets: 4,
                targetReps: 8,
                targetWeight: 185,
              },
            ],
          },
        ],
      });

      expect(newPlan).toHaveProperty('id');
      expect(newPlan.name).toBe('New Plan');
      expect(newPlan.description).toBe('Test description');
      expect(newPlan.days.length).toBe(1);
      expect(newPlan.days[0].dayName).toBe('Day One');
    });
  });

  describe('getPredefinedExercises', () => {
    it('should return all predefined exercises', async () => {
      const exercises = await getPredefinedExercises();

      expect(Array.isArray(exercises)).toBe(true);
      expect(exercises.length).toBeGreaterThan(0);
      expect(exercises[0]).toHaveProperty('id');
      expect(exercises[0]).toHaveProperty('name');
      expect(exercises[0]).toHaveProperty('category');
    });

    it('should filter exercises by category', async () => {
      const exercises = await getPredefinedExercises('CHEST');

      expect(exercises.every((e) => e.category === 'CHEST')).toBe(true);
    });
  });

  describe('getExerciseCategories', () => {
    it('should return array of exercise categories', async () => {
      const categories = await getExerciseCategories();

      expect(Array.isArray(categories)).toBe(true);
      expect(categories).toContain('CHEST');
      expect(categories).toContain('BACK');
      expect(categories).toContain('QUADS');
    });
  });

  describe('getWorkoutPlanDay', () => {
    it('should return a single workout plan day', async () => {
      const day = await getWorkoutPlanDay(10);

      expect(day.id).toBe(10);
      expect(day.dayName).toBe('Push Day');
      expect(day.exercises.length).toBeGreaterThan(0);
    });
  });

  describe('createWorkoutFromPlan', () => {
    it('should create a workout log from a plan day', async () => {
      const workout = await createWorkoutFromPlan({
        workoutPlanDayId: 10,
        exercises: [
          {
            name: 'Bench Press',
            category: 'CHEST',
            sets: 4,
            reps: 10,
            weight: 135,
            durationMinutes: null,
            caloriesBurned: null,
          },
        ],
      });

      expect(workout).toHaveProperty('id');
      expect(workout.workoutPlanDayId).toBe(10);
      expect(workout.workoutPlanDayName).toBe('Push Day');
      expect(workout.exercises.length).toBe(1);
    });
  });
});
