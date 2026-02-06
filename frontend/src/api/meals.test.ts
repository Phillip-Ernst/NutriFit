import { describe, it, expect } from 'vitest';
import { createMeal, getMyMeals } from './meals';

describe('meals API', () => {
  describe('getMyMeals', () => {
    it('returns array of meals', async () => {
      const meals = await getMyMeals();

      expect(Array.isArray(meals)).toBe(true);
      expect(meals.length).toBeGreaterThan(0);
    });

    it('returns meals with correct structure', async () => {
      const meals = await getMyMeals();
      const meal = meals[0];

      expect(meal).toHaveProperty('id');
      expect(meal).toHaveProperty('createdAt');
      expect(meal).toHaveProperty('totalCalories');
      expect(meal).toHaveProperty('totalProtein');
      expect(meal).toHaveProperty('totalCarbs');
      expect(meal).toHaveProperty('totalFats');
      expect(meal).toHaveProperty('foods');
    });

    it('returns meals with foods array', async () => {
      const meals = await getMyMeals();
      const meal = meals[0];

      expect(Array.isArray(meal.foods)).toBe(true);
      expect(meal.foods.length).toBeGreaterThan(0);
    });

    it('returns food items with correct structure', async () => {
      const meals = await getMyMeals();
      const food = meals[0].foods[0];

      expect(food).toHaveProperty('type');
      expect(food).toHaveProperty('calories');
      expect(food).toHaveProperty('protein');
      expect(food).toHaveProperty('carbs');
      expect(food).toHaveProperty('fats');
    });

    it('returns numeric totals', async () => {
      const meals = await getMyMeals();
      const meal = meals[0];

      expect(typeof meal.totalCalories).toBe('number');
      expect(typeof meal.totalProtein).toBe('number');
      expect(typeof meal.totalCarbs).toBe('number');
      expect(typeof meal.totalFats).toBe('number');
    });
  });

  describe('createMeal', () => {
    it('creates a meal and returns response', async () => {
      const request = {
        foods: [
          {
            type: 'Apple',
            calories: 95,
            protein: 0,
            carbs: 25,
            fats: 0,
          },
        ],
      };

      const response = await createMeal(request);

      expect(response).toHaveProperty('id');
      expect(response).toHaveProperty('createdAt');
      expect(response.foods).toHaveLength(1);
      expect(response.foods[0].type).toBe('Apple');
    });

    it('calculates totals correctly for single food', async () => {
      const request = {
        foods: [
          {
            type: 'Chicken Breast',
            calories: 165,
            protein: 31,
            carbs: 0,
            fats: 4,
          },
        ],
      };

      const response = await createMeal(request);

      expect(response.totalCalories).toBe(165);
      expect(response.totalProtein).toBe(31);
      expect(response.totalCarbs).toBe(0);
      expect(response.totalFats).toBe(4);
    });

    it('calculates totals correctly for multiple foods', async () => {
      const request = {
        foods: [
          {
            type: 'Rice',
            calories: 200,
            protein: 4,
            carbs: 45,
            fats: 1,
          },
          {
            type: 'Beans',
            calories: 150,
            protein: 9,
            carbs: 27,
            fats: 1,
          },
          {
            type: 'Avocado',
            calories: 160,
            protein: 2,
            carbs: 9,
            fats: 15,
          },
        ],
      };

      const response = await createMeal(request);

      expect(response.totalCalories).toBe(510);
      expect(response.totalProtein).toBe(15);
      expect(response.totalCarbs).toBe(81);
      expect(response.totalFats).toBe(17);
    });

    it('handles null nutrient values', async () => {
      const request = {
        foods: [
          {
            type: 'Unknown Food',
            calories: null,
            protein: null,
            carbs: null,
            fats: null,
          },
        ],
      };

      const response = await createMeal(request);

      expect(response.totalCalories).toBe(0);
      expect(response.totalProtein).toBe(0);
      expect(response.totalCarbs).toBe(0);
      expect(response.totalFats).toBe(0);
    });

    it('returns ISO timestamp in createdAt', async () => {
      const request = {
        foods: [
          {
            type: 'Snack',
            calories: 100,
            protein: 5,
            carbs: 10,
            fats: 3,
          },
        ],
      };

      const response = await createMeal(request);

      // Should be valid ISO date string
      expect(() => new Date(response.createdAt)).not.toThrow();
      expect(response.createdAt).toMatch(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/);
    });
  });
});
