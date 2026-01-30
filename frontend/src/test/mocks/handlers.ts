import { http, HttpResponse } from 'msw';
import type {
  WorkoutLogResponse,
  WorkoutLogRequest,
  WorkoutPlanResponse,
  WorkoutPlanRequest,
  PredefinedExercise,
  ExerciseCategory,
  WorkoutLogFromPlanRequest,
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

export const handlers = [
  // Workout Logs
  http.get('http://localhost:8080/api/workouts/mine', () => {
    return HttpResponse.json(mockWorkouts);
  }),

  http.post('http://localhost:8080/api/workouts', async ({ request }) => {
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

  http.post('http://localhost:8080/api/workouts/from-plan', async ({ request }) => {
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
  http.get('http://localhost:8080/api/workout-plans/mine', () => {
    return HttpResponse.json(mockWorkoutPlans);
  }),

  http.get('http://localhost:8080/api/workout-plans/:id', ({ params }) => {
    const id = Number(params.id);
    const plan = mockWorkoutPlans.find((p) => p.id === id);
    if (!plan) {
      return new HttpResponse(null, { status: 404 });
    }
    return HttpResponse.json(plan);
  }),

  http.post('http://localhost:8080/api/workout-plans', async ({ request }) => {
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

  http.put('http://localhost:8080/api/workout-plans/:id', async ({ params, request }) => {
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

  http.delete('http://localhost:8080/api/workout-plans/:id', () => {
    return new HttpResponse(null, { status: 204 });
  }),

  http.get('http://localhost:8080/api/workout-plans/days/:dayId', ({ params }) => {
    const dayId = Number(params.dayId);
    for (const plan of mockWorkoutPlans) {
      const day = plan.days.find((d) => d.id === dayId);
      if (day) {
        return HttpResponse.json(day);
      }
    }
    return new HttpResponse(null, { status: 404 });
  }),

  // Predefined Exercises
  http.get('http://localhost:8080/api/exercises/predefined', ({ request }) => {
    const url = new URL(request.url);
    const category = url.searchParams.get('category') as ExerciseCategory | null;
    if (category) {
      return HttpResponse.json(
        mockPredefinedExercises.filter((e) => e.category === category)
      );
    }
    return HttpResponse.json(mockPredefinedExercises);
  }),

  http.get('http://localhost:8080/api/exercises/categories', () => {
    return HttpResponse.json(mockCategories);
  }),
];
