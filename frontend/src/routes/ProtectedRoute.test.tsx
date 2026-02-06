import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { AuthContext, type AuthContextType } from '../context/AuthContext';
import ProtectedRoute from './ProtectedRoute';

function renderProtectedRoute(
  authContextValue: Partial<AuthContextType> = {},
  initialPath: string = '/protected',
) {
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
    <MemoryRouter initialEntries={[initialPath]}>
      <AuthContext.Provider value={defaultContext}>
        <Routes>
          <Route element={<ProtectedRoute />}>
            <Route path="/protected" element={<div>Protected Content</div>} />
            <Route path="/dashboard" element={<div>Dashboard Content</div>} />
          </Route>
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </AuthContext.Provider>
    </MemoryRouter>,
  );
}

describe('ProtectedRoute', () => {
  describe('when authenticated', () => {
    it('renders the outlet (protected content)', () => {
      renderProtectedRoute({
        isAuthenticated: true,
        token: 'valid-token',
        username: 'testuser',
      });

      expect(screen.getByText('Protected Content')).toBeInTheDocument();
    });

    it('does not redirect to login', () => {
      renderProtectedRoute({
        isAuthenticated: true,
        token: 'valid-token',
        username: 'testuser',
      });

      expect(screen.queryByText('Login Page')).not.toBeInTheDocument();
    });

    it('renders different protected routes correctly', () => {
      renderProtectedRoute(
        {
          isAuthenticated: true,
          token: 'valid-token',
          username: 'testuser',
        },
        '/dashboard',
      );

      expect(screen.getByText('Dashboard Content')).toBeInTheDocument();
    });
  });

  describe('when not authenticated', () => {
    it('redirects to login page', () => {
      renderProtectedRoute({ isAuthenticated: false });

      expect(screen.getByText('Login Page')).toBeInTheDocument();
    });

    it('does not render protected content', () => {
      renderProtectedRoute({ isAuthenticated: false });

      expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
    });
  });

  describe('when loading', () => {
    it('shows loading spinner', () => {
      renderProtectedRoute({ isLoading: true });

      // The LoadingSpinner component should be rendered
      // Check for the spinner's container or the spinner element itself
      const loadingContainer = document.querySelector('.min-h-screen');
      expect(loadingContainer).toBeInTheDocument();
    });

    it('does not render protected content while loading', () => {
      renderProtectedRoute({ isLoading: true });

      expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
    });

    it('does not redirect to login while loading', () => {
      renderProtectedRoute({ isLoading: true });

      expect(screen.queryByText('Login Page')).not.toBeInTheDocument();
    });
  });

  describe('authentication state transitions', () => {
    it('shows content after loading when authenticated', () => {
      // First render with loading
      const { rerender } = render(
        <MemoryRouter initialEntries={['/protected']}>
          <AuthContext.Provider
            value={{
              token: null,
              username: null,
              isAuthenticated: false,
              isLoading: true,
              login: vi.fn(),
              register: vi.fn(),
              logout: vi.fn(),
            }}
          >
            <Routes>
              <Route element={<ProtectedRoute />}>
                <Route path="/protected" element={<div>Protected Content</div>} />
              </Route>
              <Route path="/login" element={<div>Login Page</div>} />
            </Routes>
          </AuthContext.Provider>
        </MemoryRouter>,
      );

      // Should show loading, not content
      expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();

      // Rerender with authenticated state
      rerender(
        <MemoryRouter initialEntries={['/protected']}>
          <AuthContext.Provider
            value={{
              token: 'valid-token',
              username: 'testuser',
              isAuthenticated: true,
              isLoading: false,
              login: vi.fn(),
              register: vi.fn(),
              logout: vi.fn(),
            }}
          >
            <Routes>
              <Route element={<ProtectedRoute />}>
                <Route path="/protected" element={<div>Protected Content</div>} />
              </Route>
              <Route path="/login" element={<div>Login Page</div>} />
            </Routes>
          </AuthContext.Provider>
        </MemoryRouter>,
      );

      // Should now show protected content
      expect(screen.getByText('Protected Content')).toBeInTheDocument();
    });

    it('redirects to login after loading when not authenticated', () => {
      // First render with loading
      const { rerender } = render(
        <MemoryRouter initialEntries={['/protected']}>
          <AuthContext.Provider
            value={{
              token: null,
              username: null,
              isAuthenticated: false,
              isLoading: true,
              login: vi.fn(),
              register: vi.fn(),
              logout: vi.fn(),
            }}
          >
            <Routes>
              <Route element={<ProtectedRoute />}>
                <Route path="/protected" element={<div>Protected Content</div>} />
              </Route>
              <Route path="/login" element={<div>Login Page</div>} />
            </Routes>
          </AuthContext.Provider>
        </MemoryRouter>,
      );

      // Should show loading
      expect(screen.queryByText('Login Page')).not.toBeInTheDocument();

      // Rerender with unauthenticated state
      rerender(
        <MemoryRouter initialEntries={['/protected']}>
          <AuthContext.Provider
            value={{
              token: null,
              username: null,
              isAuthenticated: false,
              isLoading: false,
              login: vi.fn(),
              register: vi.fn(),
              logout: vi.fn(),
            }}
          >
            <Routes>
              <Route element={<ProtectedRoute />}>
                <Route path="/protected" element={<div>Protected Content</div>} />
              </Route>
              <Route path="/login" element={<div>Login Page</div>} />
            </Routes>
          </AuthContext.Provider>
        </MemoryRouter>,
      );

      // Should now show login page
      expect(screen.getByText('Login Page')).toBeInTheDocument();
    });
  });
});
