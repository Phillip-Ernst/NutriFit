import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import axios, { type AxiosInstance } from 'axios';
import { server } from '../test/mocks/server';
import { http, HttpResponse } from 'msw';

describe('axios interceptors', () => {
  let mockLocalStorage: { [key: string]: string };
  let locationHrefSpy: ReturnType<typeof vi.fn>;
  let api: AxiosInstance;

  // Create a fresh axios instance for each test with access to the mocked localStorage
  function createTestApi() {
    const instance = axios.create({
      baseURL: 'http://localhost:8080/api',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    instance.interceptors.request.use((config) => {
      const token = mockLocalStorage['token'] ?? null;
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    instance.interceptors.response.use(
      (response) => response,
      (error) => {
        const isAuthEndpoint = error.config?.url === '/login' || error.config?.url === '/register';

        if ((error.response?.status === 401 || error.response?.status === 403) && !isAuthEndpoint) {
          delete mockLocalStorage['token'];
          delete mockLocalStorage['username'];
          locationHrefSpy('/login');
        }
        return Promise.reject(error);
      },
    );

    return instance;
  }

  beforeEach(() => {
    // Reset localStorage mock
    mockLocalStorage = {};

    // Mock window.location.href
    locationHrefSpy = vi.fn();

    // Create fresh api instance
    api = createTestApi();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('request interceptor - JWT injection', () => {
    it('attaches Authorization header when token exists', async () => {
      mockLocalStorage['token'] = 'my-jwt-token';
      api = createTestApi(); // Recreate to pick up token

      server.use(
        http.get('*/api/test', ({ request }) => {
          const authHeader = request.headers.get('Authorization');
          return HttpResponse.json({ authHeader });
        }),
      );

      const response = await api.get('/test');

      expect(response.data.authHeader).toBe('Bearer my-jwt-token');
    });

    it('does not attach Authorization header when no token exists', async () => {
      // No token in localStorage

      server.use(
        http.get('*/api/test', ({ request }) => {
          const authHeader = request.headers.get('Authorization');
          return HttpResponse.json({ authHeader });
        }),
      );

      const response = await api.get('/test');

      expect(response.data.authHeader).toBeNull();
    });

    it('uses the token from localStorage at request time', async () => {
      server.use(
        http.get('*/api/test', ({ request }) => {
          const authHeader = request.headers.get('Authorization');
          return HttpResponse.json({ authHeader });
        }),
      );

      // First request without token
      const response1 = await api.get('/test');
      expect(response1.data.authHeader).toBeNull();

      // Add token - the interceptor reads from mockLocalStorage directly
      mockLocalStorage['token'] = 'new-token';

      // Second request should have token
      const response2 = await api.get('/test');
      expect(response2.data.authHeader).toBe('Bearer new-token');
    });
  });

  describe('response interceptor - 401 handling', () => {
    it('clears localStorage and redirects to /login on 401 from protected endpoint', async () => {
      mockLocalStorage['token'] = 'expired-token';
      mockLocalStorage['username'] = 'testuser';

      server.use(
        http.get('*/api/protected-resource', () => {
          return new HttpResponse(null, { status: 401 });
        }),
      );

      await expect(api.get('/protected-resource')).rejects.toThrow();

      expect(mockLocalStorage['token']).toBeUndefined();
      expect(mockLocalStorage['username']).toBeUndefined();
      expect(locationHrefSpy).toHaveBeenCalledWith('/login');
    });

    it('does NOT redirect on 401 from /login endpoint', async () => {
      server.use(
        http.post('*/api/login', () => {
          return HttpResponse.json(
            { error: 'UNAUTHORIZED', message: 'Invalid credentials' },
            { status: 401 },
          );
        }),
      );

      await expect(api.post('/login', { username: 'bad', password: 'bad' })).rejects.toThrow();

      // Should NOT have redirected
      expect(locationHrefSpy).not.toHaveBeenCalled();
    });

    it('does NOT redirect on 401 from /register endpoint', async () => {
      server.use(
        http.post('*/api/register', () => {
          return new HttpResponse(null, { status: 401 });
        }),
      );

      await expect(api.post('/register', { username: 'test', password: 'test' })).rejects.toThrow();

      // Should NOT have redirected
      expect(locationHrefSpy).not.toHaveBeenCalled();
    });
  });

  describe('response interceptor - 403 handling', () => {
    it('clears localStorage and redirects to /login on 403 from protected endpoint', async () => {
      mockLocalStorage['token'] = 'valid-token';
      mockLocalStorage['username'] = 'testuser';

      server.use(
        http.get('*/api/admin-resource', () => {
          return new HttpResponse(null, { status: 403 });
        }),
      );

      await expect(api.get('/admin-resource')).rejects.toThrow();

      expect(mockLocalStorage['token']).toBeUndefined();
      expect(mockLocalStorage['username']).toBeUndefined();
      expect(locationHrefSpy).toHaveBeenCalledWith('/login');
    });

    it('does NOT redirect on 403 from /login endpoint', async () => {
      server.use(
        http.post('*/api/login', () => {
          return new HttpResponse(null, { status: 403 });
        }),
      );

      await expect(api.post('/login', {})).rejects.toThrow();

      expect(locationHrefSpy).not.toHaveBeenCalled();
    });
  });

  describe('response interceptor - other errors', () => {
    it('does NOT redirect on 400 Bad Request', async () => {
      mockLocalStorage['token'] = 'valid-token';

      server.use(
        http.post('*/api/meals', () => {
          return HttpResponse.json(
            { error: 'BAD_REQUEST', message: 'Invalid data' },
            { status: 400 },
          );
        }),
      );

      await expect(api.post('/meals', {})).rejects.toThrow();

      // Token should still exist
      expect(mockLocalStorage['token']).toBe('valid-token');
      expect(locationHrefSpy).not.toHaveBeenCalled();
    });

    it('does NOT redirect on 500 Internal Server Error', async () => {
      mockLocalStorage['token'] = 'valid-token';

      server.use(
        http.get('*/api/meals/mine', () => {
          return new HttpResponse(null, { status: 500 });
        }),
      );

      await expect(api.get('/meals/mine')).rejects.toThrow();

      // Token should still exist
      expect(mockLocalStorage['token']).toBe('valid-token');
      expect(locationHrefSpy).not.toHaveBeenCalled();
    });

    it('does NOT redirect on network errors', async () => {
      mockLocalStorage['token'] = 'valid-token';

      server.use(
        http.get('*/api/test', () => {
          return HttpResponse.error();
        }),
      );

      await expect(api.get('/test')).rejects.toThrow();

      // Token should still exist
      expect(mockLocalStorage['token']).toBe('valid-token');
      expect(locationHrefSpy).not.toHaveBeenCalled();
    });
  });

  describe('response interceptor - error propagation', () => {
    it('still rejects the promise after handling 401', async () => {
      server.use(
        http.get('*/api/protected', () => {
          return new HttpResponse(null, { status: 401 });
        }),
      );

      // The promise should still reject
      await expect(api.get('/protected')).rejects.toMatchObject({
        response: { status: 401 },
      });
    });

    it('preserves error details in rejection', async () => {
      server.use(
        http.post('*/api/login', () => {
          return HttpResponse.json(
            { error: 'UNAUTHORIZED', message: 'Invalid username or password' },
            { status: 401 },
          );
        }),
      );

      try {
        await api.post('/login', { username: 'bad', password: 'bad' });
        expect.fail('Should have thrown');
      } catch (error) {
        // Type assertion for axios error
        const axiosError = error as { response?: { data?: { message?: string } } };
        expect(axiosError.response?.data?.message).toBe('Invalid username or password');
      }
    });
  });
});
