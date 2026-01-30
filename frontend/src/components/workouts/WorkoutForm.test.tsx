import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import WorkoutForm from './WorkoutForm';

function renderWithProviders() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return render(
    <BrowserRouter>
      <QueryClientProvider client={queryClient}>
        <WorkoutForm />
      </QueryClientProvider>
    </BrowserRouter>,
  );
}

describe('WorkoutForm', () => {
  it('renders initial form with one exercise row', () => {
    renderWithProviders();

    expect(screen.getByPlaceholderText('Exercise name')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Category')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Sets')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Reps')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /log workout/i })).toBeInTheDocument();
  });

  it('adds a new exercise row when Add Exercise is clicked', async () => {
    const user = userEvent.setup();
    renderWithProviders();

    const addButton = screen.getByText('+ Add Exercise');
    await user.click(addButton);

    const nameInputs = screen.getAllByPlaceholderText('Exercise name');
    expect(nameInputs).toHaveLength(2);
  });

  it('removes exercise row when delete is clicked', async () => {
    const user = userEvent.setup();
    renderWithProviders();

    const addButton = screen.getByText('+ Add Exercise');
    await user.click(addButton);

    let nameInputs = screen.getAllByPlaceholderText('Exercise name');
    expect(nameInputs).toHaveLength(2);

    const deleteButtons = screen.getAllByRole('button').filter((btn) =>
      btn.querySelector('svg'),
    );
    const trashButton = deleteButtons.find((btn) =>
      btn.className.includes('text-red'),
    );

    if (trashButton) {
      await user.click(trashButton);
    }

    nameInputs = screen.getAllByPlaceholderText('Exercise name');
    expect(nameInputs).toHaveLength(1);
  });

  it('shows validation error when exercise name is only whitespace', async () => {
    const user = userEvent.setup();
    renderWithProviders();

    const nameInput = screen.getByPlaceholderText('Exercise name');
    await user.type(nameInput, '   ');

    const submitButton = screen.getByRole('button', { name: /log workout/i });
    await user.click(submitButton);

    expect(screen.getByText('Each exercise must have a name.')).toBeInTheDocument();
  });

  it('allows typing in all input fields', async () => {
    const user = userEvent.setup();
    renderWithProviders();

    const nameInput = screen.getByPlaceholderText('Exercise name');
    const categoryInput = screen.getByPlaceholderText('Category');
    const setsInput = screen.getByPlaceholderText('Sets');
    const repsInput = screen.getByPlaceholderText('Reps');

    await user.type(nameInput, 'Bench Press');
    await user.type(categoryInput, 'Chest');
    await user.type(setsInput, '4');
    await user.type(repsInput, '10');

    expect(nameInput).toHaveValue('Bench Press');
    expect(categoryInput).toHaveValue('Chest');
    expect(setsInput).toHaveValue(4);
    expect(repsInput).toHaveValue(10);
  });

  it('cannot remove the last exercise row', () => {
    renderWithProviders();

    const deleteButtons = screen.queryAllByRole('button').filter((btn) =>
      btn.className?.includes('text-red'),
    );

    expect(deleteButtons).toHaveLength(0);
  });
});
