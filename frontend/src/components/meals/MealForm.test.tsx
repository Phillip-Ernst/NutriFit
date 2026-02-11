import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { http, HttpResponse, delay } from 'msw';
import { server } from '../../test/mocks/server';
import MealForm from './MealForm';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

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
        <MealForm />
      </QueryClientProvider>
    </BrowserRouter>,
  );
}

describe('MealForm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('rendering', () => {
    it('renders initial form with one food item row', () => {
      renderWithProviders();

      expect(screen.getByPlaceholderText('Food name')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('Cals')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('Protein')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('Carbs')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('Fats')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /log meal/i })).toBeInTheDocument();
    });

    it('renders add food item button', () => {
      renderWithProviders();

      expect(screen.getByText('+ Add Food Item')).toBeInTheDocument();
    });
  });

  describe('adding food items', () => {
    it('adds a new food item row when Add Food Item is clicked', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      const addButton = screen.getByText('+ Add Food Item');
      await user.click(addButton);

      const nameInputs = screen.getAllByPlaceholderText('Food name');
      expect(nameInputs).toHaveLength(2);
    });

    it('can add multiple food items', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      const addButton = screen.getByText('+ Add Food Item');
      await user.click(addButton);
      await user.click(addButton);
      await user.click(addButton);

      const nameInputs = screen.getAllByPlaceholderText('Food name');
      expect(nameInputs).toHaveLength(4);
    });
  });

  describe('removing food items', () => {
    it('removes food item row when delete is clicked', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      // First add another row
      const addButton = screen.getByText('+ Add Food Item');
      await user.click(addButton);

      let nameInputs = screen.getAllByPlaceholderText('Food name');
      expect(nameInputs).toHaveLength(2);

      // Find and click a delete button
      const deleteButtons = screen.getAllByRole('button').filter((btn) =>
        btn.querySelector('svg'),
      );
      const trashButton = deleteButtons.find((btn) =>
        btn.className.includes('text-red'),
      );

      if (trashButton) {
        await user.click(trashButton);
      }

      nameInputs = screen.getAllByPlaceholderText('Food name');
      expect(nameInputs).toHaveLength(1);
    });

    it('cannot remove the last food item row', () => {
      renderWithProviders();

      // With only one row, there should be no delete button visible
      const deleteButtons = screen.queryAllByRole('button').filter((btn) =>
        btn.className?.includes('text-red'),
      );

      expect(deleteButtons).toHaveLength(0);
    });
  });

  describe('form input', () => {
    it('allows typing in food name field', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      const nameInput = screen.getByPlaceholderText('Food name');
      await user.type(nameInput, 'Chicken Breast');

      expect(nameInput).toHaveValue('Chicken Breast');
    });

    it('allows typing in all nutrient fields', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      const caloriesInput = screen.getByPlaceholderText('Cals');
      const proteinInput = screen.getByPlaceholderText('Protein');
      const carbsInput = screen.getByPlaceholderText('Carbs');
      const fatsInput = screen.getByPlaceholderText('Fats');

      await user.type(caloriesInput, '165');
      await user.type(proteinInput, '31');
      await user.type(carbsInput, '0');
      await user.type(fatsInput, '4');

      expect(caloriesInput).toHaveValue(165);
      expect(proteinInput).toHaveValue(31);
      expect(carbsInput).toHaveValue(0);
      expect(fatsInput).toHaveValue(4);
    });
  });

  describe('form validation', () => {
    it('has required attribute on food name field to prevent empty submission', () => {
      renderWithProviders();

      const nameInput = screen.getByPlaceholderText('Food name');
      // Browser validation via required attribute prevents empty form submission
      expect(nameInput).toHaveAttribute('required');
    });

    it('shows validation error when food name is only whitespace', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      const nameInput = screen.getByPlaceholderText('Food name');
      // Whitespace passes HTML required but fails custom validation
      await user.type(nameInput, '   ');

      const submitButton = screen.getByRole('button', { name: /log meal/i });
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Each food item must have a name.')).toBeInTheDocument();
      });
    });

    it('validates all food items have names when one has whitespace only', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      // Add another row
      const addButton = screen.getByText('+ Add Food Item');
      await user.click(addButton);

      // Fill first food name, but second with only whitespace
      const nameInputs = screen.getAllByPlaceholderText('Food name');
      await user.type(nameInputs[0], 'Apple');
      await user.type(nameInputs[1], '   '); // Whitespace passes HTML required but fails custom validation

      const submitButton = screen.getByRole('button', { name: /log meal/i });
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Each food item must have a name.')).toBeInTheDocument();
      });
    });
  });

  describe('form submission', () => {
    it('submits successfully with valid data', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      const nameInput = screen.getByPlaceholderText('Food name');
      const caloriesInput = screen.getByPlaceholderText('Cals');
      const proteinInput = screen.getByPlaceholderText('Protein');

      await user.type(nameInput, 'Banana');
      await user.type(caloriesInput, '105');
      await user.type(proteinInput, '1');

      const submitButton = screen.getByRole('button', { name: /log meal/i });
      await user.click(submitButton);

      // Should navigate to history page on success
      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/meals/history');
      });
    });

    it('shows loading state during submission', async () => {
      // Add delay to MSW handler to catch loading state
      server.use(
        http.post('*/api/meals', async ({ request }) => {
          await delay(200);
          const body = await request.json();
          return HttpResponse.json({
            id: Date.now(),
            createdAt: new Date().toISOString(),
            totalCalories: 0,
            totalProtein: 0,
            totalCarbs: 0,
            totalFats: 0,
            foods: (body as { foods: unknown[] }).foods,
          }, { status: 201 });
        }),
      );

      const user = userEvent.setup();
      renderWithProviders();

      const nameInput = screen.getByPlaceholderText('Food name');
      await user.type(nameInput, 'Oatmeal');

      const submitButton = screen.getByRole('button', { name: /log meal/i });

      // Fire click without awaiting to catch the loading state
      user.click(submitButton);

      // Button should be in loading/disabled state during submission
      await waitFor(() => {
        expect(submitButton).toBeDisabled();
      });
    });

    it('clears error on successful submission', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      // First trigger an error by entering whitespace-only (bypasses HTML required)
      const nameInput = screen.getByPlaceholderText('Food name');
      await user.type(nameInput, '   ');

      const submitButton = screen.getByRole('button', { name: /log meal/i });
      await user.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Each food item must have a name.')).toBeInTheDocument();
      });

      // Now clear and fill in the form with valid data and submit successfully
      await user.clear(nameInput);
      await user.type(nameInput, 'Salad');
      await user.click(submitButton);

      // Error should be cleared
      await waitFor(() => {
        expect(screen.queryByText('Each food item must have a name.')).not.toBeInTheDocument();
      });
    });

    it('submits multiple food items', async () => {
      const user = userEvent.setup();
      renderWithProviders();

      // Add another row
      const addButton = screen.getByText('+ Add Food Item');
      await user.click(addButton);

      const nameInputs = screen.getAllByPlaceholderText('Food name');
      await user.type(nameInputs[0], 'Rice');
      await user.type(nameInputs[1], 'Beans');

      const submitButton = screen.getByRole('button', { name: /log meal/i });
      await user.click(submitButton);

      await waitFor(() => {
        expect(mockNavigate).toHaveBeenCalledWith('/meals/history');
      });
    });
  });
});
