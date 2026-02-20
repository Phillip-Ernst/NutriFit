import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import WorkoutCard from './WorkoutCard';
import type { WorkoutLogResponse } from '../../types';

const mockWorkout: WorkoutLogResponse = {
  id: 1,
  createdAt: '2026-01-28T10:00:00Z',
  totalDurationMinutes: 45,
  totalCaloriesBurned: 300,
  totalSets: 12,
  totalReps: 120,
  workoutPlanDayId: 10,
  workoutPlanDayName: 'Push Day',
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
      name: 'Running',
      category: 'CARDIO',
      sets: null,
      reps: null,
      weight: null,
      durationMinutes: 20,
      caloriesBurned: 200,
    },
  ],
};

describe('WorkoutCard', () => {
  it('renders workout day name and summary', () => {
    render(<WorkoutCard workout={mockWorkout} />);

    expect(screen.getByText('Push Day')).toBeInTheDocument();
    expect(screen.getByText('12 sets')).toBeInTheDocument();
    expect(screen.getByText('120 reps')).toBeInTheDocument();
  });

  it('shows calories and duration only when has cardio exercises', () => {
    render(<WorkoutCard workout={mockWorkout} />);

    expect(screen.getByText('300 cal')).toBeInTheDocument();
    expect(screen.getByText('45 min')).toBeInTheDocument();
  });

  it('hides calories and duration when no cardio exercises', () => {
    const noncardioWorkout: WorkoutLogResponse = {
      ...mockWorkout,
      exercises: [mockWorkout.exercises[0]],
    };

    render(<WorkoutCard workout={noncardioWorkout} />);

    expect(screen.queryByText('cal')).not.toBeInTheDocument();
    expect(screen.queryByText('min')).not.toBeInTheDocument();
  });

  it('shows "Workout" when no plan day name', () => {
    const noPlanWorkout: WorkoutLogResponse = {
      ...mockWorkout,
      workoutPlanDayName: null,
    };

    render(<WorkoutCard workout={noPlanWorkout} />);

    expect(screen.getByText('Workout')).toBeInTheDocument();
  });

  it('shows expand button with exercise count', () => {
    render(<WorkoutCard workout={mockWorkout} />);

    expect(screen.getByText('Show 2 exercises')).toBeInTheDocument();
  });

  it('expands to show exercises when clicked', async () => {
    const user = userEvent.setup();
    render(<WorkoutCard workout={mockWorkout} />);

    const expandButton = screen.getByText('Show 2 exercises');
    await user.click(expandButton);

    expect(screen.getByText('Bench Press')).toBeInTheDocument();
    expect(screen.getByText('Running')).toBeInTheDocument();
    expect(screen.getByText('Chest')).toBeInTheDocument();
    expect(screen.getByText('135lbs')).toBeInTheDocument();
    expect(screen.getByText('20min')).toBeInTheDocument();
  });

  it('collapses when clicked again', async () => {
    const user = userEvent.setup();
    render(<WorkoutCard workout={mockWorkout} />);

    const expandButton = screen.getByText('Show 2 exercises');
    await user.click(expandButton);

    expect(screen.getByText('Bench Press')).toBeInTheDocument();

    const hideButton = screen.getByText('Hide 2 exercises');
    await user.click(hideButton);

    expect(screen.queryByText('Bench Press')).not.toBeInTheDocument();
  });

  it('handles single exercise correctly', () => {
    const singleExerciseWorkout: WorkoutLogResponse = {
      ...mockWorkout,
      exercises: [mockWorkout.exercises[0]],
    };

    render(<WorkoutCard workout={singleExerciseWorkout} />);

    expect(screen.getByText('Show 1 exercise')).toBeInTheDocument();
  });

  it('displays per-set details when setDetails is present', async () => {
    const user = userEvent.setup();
    const workoutWithSetDetails: WorkoutLogResponse = {
      ...mockWorkout,
      exercises: [
        {
          id: 'ex-1',
          name: 'Bench Press',
          category: 'Chest',
          sets: null,
          reps: null,
          weight: null,
          durationMinutes: null,
          caloriesBurned: null,
          setDetails: [
            { id: 'set-1', setNumber: 1, reps: 10, weight: 135, completed: true },
            { id: 'set-2', setNumber: 2, reps: 8, weight: 145, completed: true },
            { id: 'set-3', setNumber: 3, reps: 6, weight: 155, completed: false },
          ],
        },
      ],
    };

    render(<WorkoutCard workout={workoutWithSetDetails} />);

    const expandButton = screen.getByText('Show 1 exercise');
    await user.click(expandButton);

    // Should show each set
    expect(screen.getByText('Set 1')).toBeInTheDocument();
    expect(screen.getByText('Set 2')).toBeInTheDocument();
    expect(screen.getByText('Set 3')).toBeInTheDocument();

    // Should show reps and weight for each set
    expect(screen.getByText('10 reps')).toBeInTheDocument();
    expect(screen.getByText('135 lbs')).toBeInTheDocument();
    expect(screen.getByText('8 reps')).toBeInTheDocument();
    expect(screen.getByText('145 lbs')).toBeInTheDocument();

    // Should show skipped indicator for incomplete set
    expect(screen.getByText('skipped')).toBeInTheDocument();
  });

  it('shows set count summary when setDetails present', async () => {
    const user = userEvent.setup();
    const workoutWithSetDetails: WorkoutLogResponse = {
      ...mockWorkout,
      exercises: [
        {
          id: 'ex-1',
          name: 'Squat',
          category: 'QUADS',
          sets: null,
          reps: null,
          weight: null,
          durationMinutes: null,
          caloriesBurned: null,
          setDetails: [
            { id: 'set-1', setNumber: 1, reps: 8, weight: 200, completed: true },
            { id: 'set-2', setNumber: 2, reps: 8, weight: 200, completed: true },
          ],
        },
      ],
    };

    render(<WorkoutCard workout={workoutWithSetDetails} />);

    const expandButton = screen.getByText('Show 1 exercise');
    await user.click(expandButton);

    // Should show "2 sets" summary instead of individual set/rep/weight
    expect(screen.getByText('2 sets')).toBeInTheDocument();
  });
});
