import { describe, it, expect } from 'vitest';
import { loginUser, registerUser } from './auth';
import { server } from '../test/mocks/server';
import { http, HttpResponse } from 'msw';

describe('auth API', () => {
  describe('loginUser', () => {
    it('returns JWT token on successful login', async () => {
      const token = await loginUser({ username: 'testuser', password: 'password123' });

      expect(typeof token).toBe('string');
      expect(token.split('.')).toHaveLength(3); // JWT format
    });

    it('throws error on invalid credentials', async () => {
      await expect(
        loginUser({ username: 'wronguser', password: 'wrongpassword' }),
      ).rejects.toThrow();
    });

    it('throws error on empty username', async () => {
      await expect(loginUser({ username: '', password: 'password123' })).rejects.toThrow();
    });

    it('returns plain text token (not JSON)', async () => {
      const token = await loginUser({ username: 'testuser', password: 'password123' });

      // Token should not be wrapped in JSON
      expect(token).not.toContain('{');
      expect(token).not.toContain('"');
    });
  });

  describe('registerUser', () => {
    it('returns user object on successful registration', async () => {
      const user = await registerUser({ username: 'newuser123', password: 'password123' });

      expect(user).toHaveProperty('id');
      expect(user).toHaveProperty('username');
      expect(user.username).toBe('newuser123');
    });

    it('throws error when username already exists', async () => {
      // testuser is pre-registered in mock handlers
      await expect(
        registerUser({ username: 'testuser', password: 'password123' }),
      ).rejects.toThrow();
    });

    it('returns user with numeric id', async () => {
      const user = await registerUser({ username: 'anotherunique', password: 'password123' });

      expect(typeof user.id).toBe('number');
    });
  });

  describe('error handling', () => {
    it('handles network errors gracefully', async () => {
      server.use(
        http.post('*/api/login', () => {
          return HttpResponse.error();
        }),
      );

      await expect(
        loginUser({ username: 'testuser', password: 'password123' }),
      ).rejects.toThrow();
    });

    it('handles server errors (500)', async () => {
      server.use(
        http.post('*/api/register', () => {
          return new HttpResponse(null, { status: 500 });
        }),
      );

      await expect(
        registerUser({ username: 'newuser', password: 'password123' }),
      ).rejects.toThrow();
    });
  });
});
