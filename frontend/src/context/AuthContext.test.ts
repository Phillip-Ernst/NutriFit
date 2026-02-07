import { describe, it, expect } from 'vitest';
import { isTokenExpired } from './AuthContext';

// Helper to create a mock JWT token with a specific expiry
function createMockToken(expInSeconds: number): string {
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const payload = btoa(JSON.stringify({ sub: 'testuser', exp: expInSeconds }));
  const signature = 'mock-signature';
  return `${header}.${payload}.${signature}`;
}

describe('isTokenExpired', () => {
  it('returns false for a valid non-expired token', () => {
    // Token expires 1 hour from now
    const futureExp = Math.floor(Date.now() / 1000) + 3600;
    const token = createMockToken(futureExp);

    expect(isTokenExpired(token)).toBe(false);
  });

  it('returns true for an expired token', () => {
    // Token expired 1 hour ago
    const pastExp = Math.floor(Date.now() / 1000) - 3600;
    const token = createMockToken(pastExp);

    expect(isTokenExpired(token)).toBe(true);
  });

  it('returns true for a token expiring within buffer time (10 seconds)', () => {
    // Token expires in 5 seconds (within 10 second buffer)
    const soonExp = Math.floor(Date.now() / 1000) + 5;
    const token = createMockToken(soonExp);

    expect(isTokenExpired(token)).toBe(true);
  });

  it('returns false for a token expiring just outside buffer time', () => {
    // Token expires in 15 seconds (outside 10 second buffer)
    const soonExp = Math.floor(Date.now() / 1000) + 15;
    const token = createMockToken(soonExp);

    expect(isTokenExpired(token)).toBe(false);
  });

  it('returns true for invalid JWT format (missing parts)', () => {
    expect(isTokenExpired('invalid-token')).toBe(true);
    expect(isTokenExpired('only.two')).toBe(true);
    expect(isTokenExpired('')).toBe(true);
  });

  it('returns true for invalid base64 in payload', () => {
    const token = 'valid-header.!!!invalid-base64!!!.signature';
    expect(isTokenExpired(token)).toBe(true);
  });

  it('returns true for token without exp claim', () => {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
    const payload = btoa(JSON.stringify({ sub: 'testuser' })); // No exp
    const token = `${header}.${payload}.signature`;

    expect(isTokenExpired(token)).toBe(true);
  });

  it('returns true for malformed JSON in payload', () => {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
    const payload = btoa('not valid json');
    const token = `${header}.${payload}.signature`;

    expect(isTokenExpired(token)).toBe(true);
  });
});
