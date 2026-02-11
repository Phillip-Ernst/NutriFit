import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { AuthContext, type AuthContextType } from '../context/AuthContext';
import LoginPage from './LoginPage';

const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

function renderLoginPage(authContextValue: Partial<AuthContextType> = {}) {
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
    <MemoryRouter initialEntries={['/login']}>
      <AuthContext.Provider value={defaultContext}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<div>Dashboard</div>} />
        </Routes>
      </AuthContext.Provider>
    </MemoryRouter>,
  );
}

describe('LoginPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('rendering', () => {
    it('renders login form with username and password fields', () => {
      renderLoginPage();

      expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    });

    it('renders NutriFit branding', () => {
      renderLoginPage();

      expect(screen.getByText('NutriFit')).toBeInTheDocument();
      expect(screen.getByText('Sign in to your account')).toBeInTheDocument();
    });

    it('renders link to register page', () => {
      renderLoginPage();

      expect(screen.getByRole('link', { name: /register/i })).toBeInTheDocument();
      expect(screen.getByRole('link', { name: /register/i })).toHaveAttribute(
        'href',
        '/register',
      );
    });

    it('renders loading spinner while auth is loading', () => {
      renderLoginPage({ isLoading: true });

      expect(screen.getByRole('status')).toBeInTheDocument();
      expect(screen.queryByLabelText(/username/i)).not.toBeInTheDocument();
    });
  });

  describe('redirection', () => {
    it('redirects to dashboard if already authenticated', () => {
      renderLoginPage({ isAuthenticated: true, token: 'test-token' });

      expect(screen.queryByLabelText(/username/i)).not.toBeInTheDocument();
    });
  });

  describe('form submission', () => {
    it('calls login with username and password on submit', async () => {
      const mockLogin = vi.fn().mockResolvedValue(undefined);
      const user = userEvent.setup();

      renderLoginPage({ login: mockLogin });

      await user.type(screen.getByLabelText(/username/i), 'testuser');
      await user.type(screen.getByLabelText(/password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /login/i }));

      await waitFor(() => {
        expect(mockLogin).toHaveBeenCalledWith('testuser', 'password123');
      });
    });

    it('shows loading state during login', async () => {
      const mockLogin = vi.fn(
        (_username: string, _password: string): Promise<void> =>
          new Promise((resolve) => setTimeout(resolve, 100)),
      );
      const user = userEvent.setup();

      renderLoginPage({ login: mockLogin });

      await user.type(screen.getByLabelText(/username/i), 'testuser');
      await user.type(screen.getByLabelText(/password/i), 'password123');
      await user.click(screen.getByRole('button', { name: /login/i }));

      // Button should be in loading state
      const button = screen.getByRole('button', { name: /login/i });
      expect(button).toBeDisabled();
    });

    it('displays error message with register suggestion on login failure', async () => {
      const mockLogin = vi.fn().mockRejectedValue(new Error('Invalid credentials'));
      const user = userEvent.setup();

      renderLoginPage({ login: mockLogin });

      await user.type(screen.getByLabelText(/username/i), 'wronguser');
      await user.type(screen.getByLabelText(/password/i), 'wrongpassword');
      await user.click(screen.getByRole('button', { name: /login/i }));

      await waitFor(() => {
        expect(screen.getByText('Invalid username or password.')).toBeInTheDocument();
      });

      // Verify the error box contains a register link
      const registerLink = screen.getByRole('link', { name: /register here/i });
      expect(registerLink).toBeInTheDocument();
      expect(registerLink).toHaveAttribute('href', '/register');
    });

    it('clears error message on new form submission', async () => {
      const mockLogin = vi
        .fn()
        .mockRejectedValueOnce(new Error('Invalid credentials'))
        .mockResolvedValueOnce(undefined);
      const user = userEvent.setup();

      renderLoginPage({ login: mockLogin });

      // First attempt - fails
      await user.type(screen.getByLabelText(/username/i), 'wronguser');
      await user.type(screen.getByLabelText(/password/i), 'wrongpassword');
      await user.click(screen.getByRole('button', { name: /login/i }));

      await waitFor(() => {
        expect(screen.getByText('Invalid username or password.')).toBeInTheDocument();
      });

      // Second attempt - clear fields and try again
      await user.clear(screen.getByLabelText(/username/i));
      await user.clear(screen.getByLabelText(/password/i));
      await user.type(screen.getByLabelText(/username/i), 'correctuser');
      await user.type(screen.getByLabelText(/password/i), 'correctpassword');
      await user.click(screen.getByRole('button', { name: /login/i }));

      // Error should be cleared during submission
      await waitFor(() => {
        expect(screen.queryByText('Invalid username or password.')).not.toBeInTheDocument();
      });
    });
  });

  describe('input fields', () => {
    it('allows typing in username field', async () => {
      const user = userEvent.setup();
      renderLoginPage();

      const usernameInput = screen.getByLabelText(/username/i);
      await user.type(usernameInput, 'myusername');

      expect(usernameInput).toHaveValue('myusername');
    });

    it('allows typing in password field', async () => {
      const user = userEvent.setup();
      renderLoginPage();

      const passwordInput = screen.getByLabelText(/password/i);
      await user.type(passwordInput, 'mypassword');

      expect(passwordInput).toHaveValue('mypassword');
    });

    it('has correct input types', () => {
      renderLoginPage();

      expect(screen.getByLabelText(/username/i)).toHaveAttribute('type', 'text');
      expect(screen.getByLabelText(/password/i)).toHaveAttribute('type', 'password');
    });

    it('has required attribute on inputs', () => {
      renderLoginPage();

      expect(screen.getByLabelText(/username/i)).toBeRequired();
      expect(screen.getByLabelText(/password/i)).toBeRequired();
    });
  });
});
