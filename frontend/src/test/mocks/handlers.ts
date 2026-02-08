import { http, HttpResponse } from 'msw';
import type {
  WorkoutLogResponse,
  WorkoutLogRequest,
  WorkoutPlanResponse,
  WorkoutPlanRequest,
  PredefinedExercise,
  ExerciseCategory,
  WorkoutLogFromPlanRequest,
  MealLogResponse,
  MealLogRequest,
  LoginRequest,
  RegisterRequest,
  User,
} from '../../types';

const mockWorkouts: WorkoutLogResponse[] = [
  {
    id: 1,
    createdAt: '2026-01-28T10:00:00Z',
    totalDurationMinutes: 45,
    totalCaloriesBurned: 300,
    totalSets: 12,
    totalReps: 120,
    workoutPlanDayId: null,
    workoutPlanDayName: null,
    exercises: [
      {
        name: 'Bench Press',
        category: 'Chest',
        sets: 4,
        reps: 10,
        weight: 135,
        durationMinutes: null,
        caloriesBurned: null,
      },
      {
        name: 'Squats',
        category: 'Legs',
        sets: 4,
        reps: 10,
        weight: 185,
        durationMinutes: null,
        caloriesBurned: null,
      },
      {
        name: 'Running',
        category: 'Cardio',
        sets: null,
        reps: null,
        weight: null,
        durationMinutes: 20,
        caloriesBurned: 200,
      },
    ],
  },
  {
    id: 2,
    createdAt: '2026-01-27T09:00:00Z',
    totalDurationMinutes: 30,
    totalCaloriesBurned: 200,
    totalSets: 8,
    totalReps: 80,
    workoutPlanDayId: 10,
    workoutPlanDayName: 'Push Day',
    exercises: [
      {
        name: 'Pull-ups',
        category: 'Back',
        sets: 4,
        reps: 8,
        weight: null,
        durationMinutes: null,
        caloriesBurned: null,
      },
      {
        name: 'Deadlift',
        category: 'Back',
        sets: 4,
        reps: 6,
        weight: 225,
        durationMinutes: null,
        caloriesBurned: null,
      },
    ],
  },
];

const mockWorkoutPlans: WorkoutPlanResponse[] = [
  {
    id: 1,
    name: 'PPL Split',
    description: 'Push Pull Legs workout plan',
    createdAt: '2026-01-25T10:00:00Z',
    days: [
      {
        id: 10,
        dayNumber: 1,
        dayName: 'Push Day',
        exercises: [
          {
            name: 'Bench Press',
            category: 'CHEST',
            isCustom: false,
            targetSets: 4,
            targetReps: 10,
            targetWeight: 135,
          },
          {
            name: 'Overhead Press',
            category: 'SHOULDERS',
            isCustom: false,
            targetSets: 3,
            targetReps: 8,
            targetWeight: 95,
          },
        ],
      },
      {
        id: 11,
        dayNumber: 2,
        dayName: 'Pull Day',
        exercises: [
          {
            name: 'Deadlift',
            category: 'BACK',
            isCustom: false,
            targetSets: 4,
            targetReps: 6,
            targetWeight: 225,
          },
        ],
      },
      {
        id: 12,
        dayNumber: 3,
        dayName: 'Leg Day',
        exercises: [
          {
            name: 'Squat',
            category: 'QUADS',
            isCustom: false,
            targetSets: 4,
            targetReps: 8,
            targetWeight: 185,
          },
        ],
      },
    ],
  },
];

const mockPredefinedExercises: PredefinedExercise[] = [
  { id: 'BENCH_PRESS', name: 'Bench Press', category: 'CHEST' },
  { id: 'SQUAT', name: 'Squat', category: 'QUADS' },
  { id: 'DEADLIFT', name: 'Deadlift', category: 'BACK' },
  { id: 'PULL_UP', name: 'Pull-up', category: 'BACK' },
  { id: 'OVERHEAD_PRESS', name: 'Overhead Press', category: 'SHOULDERS' },
];

const mockCategories: ExerciseCategory[] = [
  'BACK',
  'CHEST',
  'BICEPS',
  'TRICEPS',
  'SHOULDERS',
  'HAMSTRINGS',
  'QUADS',
  'GLUTES',
  'CALVES',
  'CORE',
  'CARDIO',
  'OTHER',
];

// Mock JWT token for testing (expires in 1 hour)
const createMockToken = (username: string): string => {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = btoa(
    JSON.stringify({
      sub: username,
      exp: Math.floor(Date.now() / 1000) + 3600,
    }),
  );
  return `${header}.${payload}.mock-signature`;
};

const mockMeals: MealLogResponse[] = [
  {
    id: 1,
    createdAt: '2026-01-28T08:00:00Z',
    totalCalories: 450,
    totalProtein: 35,
    totalCarbs: 45,
    totalFats: 15,
    foods: [
      {
        type: 'Oatmeal',
        calories: 150,
        protein: 5,
        carbs: 27,
        fats: 3,
      },
      {
        type: 'Eggs',
        calories: 200,
        protein: 24,
        carbs: 2,
        fats: 10,
      },
      {
        type: 'Banana',
        calories: 100,
        protein: 1,
        carbs: 26,
        fats: 0,
      },
    ],
  },
  {
    id: 2,
    createdAt: '2026-01-28T12:30:00Z',
    totalCalories: 650,
    totalProtein: 45,
    totalCarbs: 60,
    totalFats: 20,
    foods: [
      {
        type: 'Chicken Breast',
        calories: 250,
        protein: 40,
        carbs: 0,
        fats: 8,
      },
      {
        type: 'Brown Rice',
        calories: 200,
        protein: 4,
        carbs: 45,
        fats: 2,
      },
      {
        type: 'Broccoli',
        calories: 50,
        protein: 3,
        carbs: 10,
        fats: 0,
      },
    ],
  },
];

// Mock registered users for testing
const mockUsers: Map<string, { id: number; username: string; password: string }> = new Map([
  ['testuser', { id: 1, username: 'testuser', password: 'password123' }],
]);

export const handlers = [
  // Auth
  http.post('*/api/login', async ({ request }) => {
    const body = (await request.json()) as LoginRequest;

    const user = mockUsers.get(body.username);
    if (!user || user.password !== body.password) {
      return HttpResponse.json(
        { error: 'UNAUTHORIZED', message: 'Invalid username or password' },
        { status: 401 },
      );
    }

    // Return JSON with token field (matching updated backend behavior)
    return HttpResponse.json({ token: createMockToken(body.username) });
  }),

  http.post('*/api/register', async ({ request }) => {
    const body = (await request.json()) as RegisterRequest;

    if (mockUsers.has(body.username)) {
      return HttpResponse.json(
        { error: 'USERNAME_EXISTS', message: 'Username already taken' },
        { status: 409 },
      );
    }

    const newUser: User = {
      id: mockUsers.size + 1,
      username: body.username,
    };

    // Add to mock users for subsequent login
    mockUsers.set(body.username, { ...newUser, password: body.password });

    return HttpResponse.json(newUser, { status: 201 });
  }),

  // Meals
  http.get('*/api/meals/mine', () => {
    return HttpResponse.json(mockMeals);
  }),

  http.post('*/api/meals', async ({ request }) => {
    const body = (await request.json()) as MealLogRequest;

    const totalCalories = body.foods.reduce((sum, food) => sum + (food.calories ?? 0), 0);
    const totalProtein = body.foods.reduce((sum, food) => sum + (food.protein ?? 0), 0);
    const totalCarbs = body.foods.reduce((sum, food) => sum + (food.carbs ?? 0), 0);
    const totalFats = body.foods.reduce((sum, food) => sum + (food.fats ?? 0), 0);

    const response: MealLogResponse = {
      id: Date.now(),
      createdAt: new Date().toISOString(),
      totalCalories,
      totalProtein,
      totalCarbs,
      totalFats,
      foods: body.foods,
    };

    return HttpResponse.json(response, { status: 201 });
  }),

  // Workout Logs
  http.get('*/api/workouts/mine', () => {
    return HttpResponse.json(mockWorkouts);
  }),

  http.post('*/api/workouts', async ({ request }) => {
    const body = (await request.json()) as WorkoutLogRequest;

    const totalDurationMinutes = body.exercises.reduce(
      (sum, ex) => sum + (ex.durationMinutes ?? 0),
      0,
    );
    const totalCaloriesBurned = body.exercises.reduce(
      (sum, ex) => sum + (ex.caloriesBurned ?? 0),
      0,
    );
    const totalSets = body.exercises.reduce((sum, ex) => sum + (ex.sets ?? 0), 0);
    const totalReps = body.exercises.reduce((sum, ex) => sum + (ex.reps ?? 0), 0);

    const response: WorkoutLogResponse = {
      id: Date.now(),
      createdAt: new Date().toISOString(),
      totalDurationMinutes,
      totalCaloriesBurned,
      totalSets,
      totalReps,
      workoutPlanDayId: null,
      workoutPlanDayName: null,
      exercises: body.exercises,
    };

    return HttpResponse.json(response, { status: 201 });
  }),

  http.post('*/api/workouts/from-plan', async ({ request }) => {
    const body = (await request.json()) as WorkoutLogFromPlanRequest;

    const totalDurationMinutes = body.exercises.reduce(
      (sum, ex) => sum + (ex.durationMinutes ?? 0),
      0,
    );
    const totalCaloriesBurned = body.exercises.reduce(
      (sum, ex) => sum + (ex.caloriesBurned ?? 0),
      0,
    );
    const totalSets = body.exercises.reduce((sum, ex) => sum + (ex.sets ?? 0), 0);
    const totalReps = body.exercises.reduce((sum, ex) => sum + (ex.reps ?? 0), 0);

    const planDay = mockWorkoutPlans[0]?.days.find((d) => d.id === body.workoutPlanDayId);

    const response: WorkoutLogResponse = {
      id: Date.now(),
      createdAt: new Date().toISOString(),
      totalDurationMinutes,
      totalCaloriesBurned,
      totalSets,
      totalReps,
      workoutPlanDayId: body.workoutPlanDayId,
      workoutPlanDayName: planDay?.dayName ?? null,
      exercises: body.exercises,
    };

    return HttpResponse.json(response, { status: 201 });
  }),

  // Workout Plans
  http.get('*/api/workout-plans/mine', () => {
    return HttpResponse.json(mockWorkoutPlans);
  }),

  http.get('*/api/workout-plans/days/:dayId', ({ params }) => {
    const dayId = Number(params.dayId);
    for (const plan of mockWorkoutPlans) {
      const day = plan.days.find((d) => d.id === dayId);
      if (day) {
        return HttpResponse.json(day);
      }
    }
    return new HttpResponse(null, { status: 404 });
  }),

  http.get('*/api/workout-plans/:id', ({ params }) => {
    const id = Number(params.id);
    const plan = mockWorkoutPlans.find((p) => p.id === id);
    if (!plan) {
      return new HttpResponse(null, { status: 404 });
    }
    return HttpResponse.json(plan);
  }),

  http.post('*/api/workout-plans', async ({ request }) => {
    const body = (await request.json()) as WorkoutPlanRequest;
    const response: WorkoutPlanResponse = {
      id: Date.now(),
      name: body.name,
      description: body.description,
      createdAt: new Date().toISOString(),
      days: body.days.map((day, idx) => ({
        id: Date.now() + idx,
        dayNumber: day.dayNumber,
        dayName: day.dayName,
        exercises: day.exercises,
      })),
    };
    return HttpResponse.json(response, { status: 201 });
  }),

  http.put('*/api/workout-plans/:id', async ({ params, request }) => {
    const id = Number(params.id);
    const body = (await request.json()) as WorkoutPlanRequest;
    const response: WorkoutPlanResponse = {
      id,
      name: body.name,
      description: body.description,
      createdAt: new Date().toISOString(),
      days: body.days.map((day, idx) => ({
        id: Date.now() + idx,
        dayNumber: day.dayNumber,
        dayName: day.dayName,
        exercises: day.exercises,
      })),
    };
    return HttpResponse.json(response);
  }),

  http.delete('*/api/workout-plans/:id', () => {
    return new HttpResponse(null, { status: 204 });
  }),

  // Predefined Exercises
  http.get('*/api/exercises/predefined', ({ request }) => {
    const url = new URL(request.url);
    const category = url.searchParams.get('category') as ExerciseCategory | null;
    if (category) {
      return HttpResponse.json(
        mockPredefinedExercises.filter((e) => e.category === category)
      );
    }
    return HttpResponse.json(mockPredefinedExercises);
  }),

  http.get('*/api/exercises/categories', () => {
    return HttpResponse.json(mockCategories);
  }),
];
