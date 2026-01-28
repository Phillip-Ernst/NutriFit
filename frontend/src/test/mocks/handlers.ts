import { http, HttpResponse } from 'msw';
import type { WorkoutLogResponse, WorkoutLogRequest } from '../../types';

const mockWorkouts: WorkoutLogResponse[] = [
  {
    id: 1,
    createdAt: '2026-01-28T10:00:00Z',
    totalDurationMinutes: 45,
    totalCaloriesBurned: 300,
    totalSets: 12,
    totalReps: 120,
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

export const handlers = [
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
      exercises: body.exercises,
    };

    return HttpResponse.json(response, { status: 201 });
  }),
];
