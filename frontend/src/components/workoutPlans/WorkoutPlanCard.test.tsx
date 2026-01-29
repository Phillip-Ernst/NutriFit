import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import WorkoutPlanCard from './WorkoutPlanCard';
import type { WorkoutPlanResponse } from '../../types';

const mockPlan: WorkoutPlanResponse = {
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
};

const renderWithRouter = (ui: React.ReactElement) => {
  return render(<BrowserRouter>{ui}</BrowserRouter>);
};

describe('WorkoutPlanCard', () => {
  it('renders plan name and description', () => {
    renderWithRouter(<WorkoutPlanCard plan={mockPlan} />);

    expect(screen.getByText('PPL Split')).toBeInTheDocument();
    expect(screen.getByText('Push Pull Legs workout plan')).toBeInTheDocument();
  });

  it('renders day count correctly', () => {
    renderWithRouter(<WorkoutPlanCard plan={mockPlan} />);

    expect(screen.getByText('3 days')).toBeInTheDocument();
  });

  it('renders exercise count correctly', () => {
    renderWithRouter(<WorkoutPlanCard plan={mockPlan} />);

    expect(screen.getByText('4 exercises')).toBeInTheDocument();
  });

  it('renders day names as tags', () => {
    renderWithRouter(<WorkoutPlanCard plan={mockPlan} />);

    expect(screen.getByText('Push Day')).toBeInTheDocument();
    expect(screen.getByText('Pull Day')).toBeInTheDocument();
    expect(screen.getByText('Leg Day')).toBeInTheDocument();
  });

  it('links to plan detail page', () => {
    renderWithRouter(<WorkoutPlanCard plan={mockPlan} />);

    const link = screen.getByRole('link');
    expect(link).toHaveAttribute('href', '/workouts/plans/1');
  });

  it('handles singular day count', () => {
    const singleDayPlan = {
      ...mockPlan,
      days: [mockPlan.days[0]],
    };

    renderWithRouter(<WorkoutPlanCard plan={singleDayPlan} />);

    expect(screen.getByText('1 day')).toBeInTheDocument();
  });

  it('handles singular exercise count', () => {
    const singleExercisePlan = {
      ...mockPlan,
      days: [
        {
          ...mockPlan.days[0],
          exercises: [mockPlan.days[0].exercises[0]],
        },
      ],
    };

    renderWithRouter(<WorkoutPlanCard plan={singleExercisePlan} />);

    expect(screen.getByText('1 exercise')).toBeInTheDocument();
  });

  it('handles plan without description', () => {
    const planNoDesc = {
      ...mockPlan,
      description: null,
    };

    renderWithRouter(<WorkoutPlanCard plan={planNoDesc} />);

    expect(screen.getByText('PPL Split')).toBeInTheDocument();
    expect(screen.queryByText('Push Pull Legs workout plan')).not.toBeInTheDocument();
  });
});
