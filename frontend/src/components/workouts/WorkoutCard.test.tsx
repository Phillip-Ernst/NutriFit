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
      name: 'Running',
      category: 'Cardio',
      sets: null,
      reps: null,
      weight: null,
      durationMinutes: 20,
      caloriesBurned: 200,
    },
  ],
};

describe('WorkoutCard', () => {
  it('renders workout summary', () => {
    render(<WorkoutCard workout={mockWorkout} />);

    expect(screen.getByText('45')).toBeInTheDocument();
    expect(screen.getByText('min')).toBeInTheDocument();
    expect(screen.getByText('300 cal')).toBeInTheDocument();
    expect(screen.getByText('12 sets')).toBeInTheDocument();
    expect(screen.getByText('120 reps')).toBeInTheDocument();
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
});
