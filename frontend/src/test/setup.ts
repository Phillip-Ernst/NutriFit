import '@testing-library/jest-dom';
import { server } from './mocks/server';

// Mock localStorage
const localStorageMock = {
  getItem: () => 'test-token',
  setItem: () => {},
  removeItem: () => {},
  clear: () => {},
  length: 0,
  key: () => null,
};

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
