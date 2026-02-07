import { createContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser, registerUser } from '../api/auth';

export interface AuthContextType {
  token: string | null;
  username: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

/**
 * Checks if a JWT token is expired by decoding the payload and comparing exp claim
 * Returns true if token is expired or invalid, false if still valid
 */
export function isTokenExpired(token: string): boolean {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) {
      return true; // Invalid JWT format
    }
    const payload = JSON.parse(atob(parts[1]));
    if (!payload.exp) {
      return true; // No expiry claim
    }
    // exp is in seconds, Date.now() is in milliseconds
    const expiryTime = payload.exp * 1000;
    // Add small buffer (10 seconds) to account for clock skew
    return Date.now() >= expiryTime - 10000;
  } catch {
    return true; // Invalid token
  }
}

export const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUsername = localStorage.getItem('username');

    if (storedToken && storedUsername) {
      // Validate token expiry before restoring session
      if (isTokenExpired(storedToken)) {
        // Token expired - clear storage
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        console.info('Session expired. Please log in again.');
      } else {
        setToken(storedToken);
        setUsername(storedUsername);
      }
    }
    setIsLoading(false);
  }, []);

  const login = useCallback(async (user: string, password: string) => {
    const jwt = await loginUser({ username: user, password });
    localStorage.setItem('token', jwt);
    localStorage.setItem('username', user);
    setToken(jwt);
    setUsername(user);
    navigate('/dashboard');
  }, [navigate]);

  const register = useCallback(async (user: string, password: string) => {
    await registerUser({ username: user, password });
    await login(user, password);
  }, [login]);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setToken(null);
    setUsername(null);
    navigate('/login');
  }, [navigate]);

  return (
    <AuthContext.Provider
      value={{
        token,
        username,
        isAuthenticated: !!token,
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
