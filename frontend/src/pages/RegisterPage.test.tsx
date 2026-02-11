import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { AuthContext, type AuthContextType } from '../context/AuthContext';
import RegisterPage from './RegisterPage';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

function renderRegisterPage(authContextValue: Partial<AuthContextType> = {}) {
  const defaultContext: AuthContextType = {
    token: null,
    username: null,
    isAuthenticated: false,
    isLoading: false,
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    ...authContextValue,
  };

  return render(
    <MemoryRouter initialEntries={['/register']}>
      <AuthContext.Provider value={defaultContext}>
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/dashboard" element={<div>Dashboard</div>} />
          <Route path="/login" element={<div>Login</div>} />
        </Routes>
      </AuthContext.Provider>
    </MemoryRouter>,
  );
}

describe('RegisterPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('rendering', () => {
    it('renders registration form with all fields', () => {
      renderRegisterPage();

      expect(screen.getByLabelText(/^username$/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/^password$/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/confirm password/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /create account/i })).toBeInTheDocument();
    });

    it('renders NutriFit branding', () => {
      renderRegisterPage();

      expect(screen.getByText('NutriFit')).toBeInTheDocument();
      expect(screen.getByText('Create your account')).toBeInTheDocument();
    });

    it('renders link to login page', () => {
      renderRegisterPage();

      expect(screen.getByRole('link', { name: /login/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /login/i })).toHaveAttribute(
        'href',
        '/login',
      );
    });

    it('renders loading spinner while auth is loading', () => {
      renderRegisterPage({ isLoading: true });

      expect(screen.getByRole('status')).toBeInTheDocument();
      expect(screen.queryByLabelText(/username/i)).not.toBeInTheDocument();
    });
  });

  describe('redirection', () => {
    it('redirects to dashboard if already authenticated', () => {
      renderRegisterPage({ isAuthenticated: true, token: 'test-token' });

      expect(screen.queryByLabelText(/username/i)).not.toBeInTheDocument();
    });
  });

  describe('form validation', () => {
    it('shows error when password is less than 6 characters', async () => {
      const user = userEvent.setup();
      renderRegisterPage();

      await user.type(screen.getByLabelText(/^username$/i), 'newuser');
      await user.type(screen.getByLabelText(/^password$/i), '12345');
      await user.type(screen.getByLabelText(/confirm password/i), '12345');
      await user.click(screen.getByRole('button', { name: /create account/i }));

      expect(screen.getByText('Password must be at least 6 characters.')).toBeInTheDocument();
    });

    it('shows error when passwords do not match', async () => {
      const user = userEvent.setup();
      renderRegisterPage();

      await user.type(screen.getByLabelText(/^username$/i), 'newuser');
      await user.type(screen.getByLabelText(/^password$/i), 'password123');
      await user.type(screen.getByLabelText(/confirm password/i), 'password456');
      await user.click(screen.getByRole('button', { name: /create account/i }));

      expect(screen.getByText('Passwords do not match.')).toBeInTheDocument();
    });

    it('does not call register when validation fails', async () => {
      const mockRegister = vi.fn();
      const user = userEvent.setup();
      renderRegisterPage({ register: mockRegister });

      await user.type(screen.getByLabelText(/^username$/i), 'newuser');
      await user.type(screen.getByLabelText(/^password$/i), '12345');
      await user.type(screen.getByLabelText(/confirm password/i), '12345');
      await user.click(screen.getByRole('button', { name: /create account/i }));

      expect(mockRegister).not.toHaveBeenCalled();
    });
  });

  describe('form submission', () => {
    it('calls register with username and password on valid submit', async () => {
      const mockRegister = vi.fn().mockResolvedValue(undefined);
      const user = userEvent.setup();

      renderRegisterPage({ register: mockRegister });

      await user.type(screen.getByLabelText(/^username$/i), 'newuser');
      await user.type(screen.getByLabelText(/^password$/i), 'password123');
      await user.type(screen.getByLabelText(/confirm password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /create account/i }));

      await waitFor(() => {
        expect(mockRegister).toHaveBeenCalledWith('newuser', 'password123');
      });
    });

    it('shows loading state during registration', async () => {
      const mockRegister = vi.fn(
        (_username: string, _password: string): Promise<void> =>
          new Promise((resolve) => setTimeout(resolve, 100)),
      );
      const user = userEvent.setup();

      renderRegisterPage({ register: mockRegister });

      await user.type(screen.getByLabelText(/^username$/i), 'newuser');
      await user.type(screen.getByLabelText(/^password$/i), 'password123');
      await user.type(screen.getByLabelText(/confirm password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /create account/i }));

      const button = screen.getByRole('button', { name: /create account/i });
      expect(button).toBeDisabled();
    });

    it('displays error message when username already exists', async () => {
      const mockRegister = vi.fn().mockRejectedValue(new Error('Username already taken'));
      const user = userEvent.setup();

      renderRegisterPage({ register: mockRegister });

      await user.type(screen.getByLabelText(/^username$/i), 'existinguser');
      await user.type(screen.getByLabelText(/^password$/i), 'password123');
      await user.type(screen.getByLabelText(/confirm password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /create account/i }));

      await waitFor(() => {
        expect(screen.getByText(/username already taken/i)).toBeInTheDocument();
      });
    });

    it('displays generic error for unknown errors', async () => {
      const mockRegister = vi.fn().mockRejectedValue('Unknown error');
      const user = userEvent.setup();

      renderRegisterPage({ register: mockRegister });

      await user.type(screen.getByLabelText(/^username$/i), 'newuser');
      await user.type(screen.getByLabelText(/^password$/i), 'password123');
      await user.type(screen.getByLabelText(/confirm password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /create account/i }));

      await waitFor(() => {
        expect(
          screen.getByText(/registration failed/i),
        ).toBeInTheDocument();
      });
    });
  });

  describe('input fields', () => {
    it('allows typing in all fields', async () => {
      const user = userEvent.setup();
      renderRegisterPage();

      const usernameInput = screen.getByLabelText(/^username$/i);
      const passwordInput = screen.getByLabelText(/^password$/i);
      const confirmInput = screen.getByLabelText(/confirm password/i);

      await user.type(usernameInput, 'myusername');
      await user.type(passwordInput, 'mypassword');
      await user.type(confirmInput, 'mypassword');

      expect(usernameInput).toHaveValue('myusername');
      expect(passwordInput).toHaveValue('mypassword');
      expect(confirmInput).toHaveValue('mypassword');
    });

    it('has correct input types', () => {
      renderRegisterPage();

      expect(screen.getByLabelText(/^username$/i)).toHaveAttribute('type', 'text');
      expect(screen.getByLabelText(/^password$/i)).toHaveAttribute('type', 'password');
      expect(screen.getByLabelText(/confirm password/i)).toHaveAttribute('type', 'password');
    });

    it('has required attribute on all inputs', () => {
      renderRegisterPage();

      expect(screen.getByLabelText(/^username$/i)).toBeRequired();
      expect(screen.getByLabelText(/^password$/i)).toBeRequired();
      expect(screen.getByLabelText(/confirm password/i)).toBeRequired();
    });

    it('shows helpful placeholder text', () => {
      renderRegisterPage();

      expect(screen.getByPlaceholderText(/choose a username/i)).toBeInTheDocument();
      expect(screen.getByPlaceholderText(/at least 6 characters/i)).toBeInTheDocument();
      expect(screen.getByPlaceholderText(/re-enter your password/i)).toBeInTheDocument();
    });
  });
});
