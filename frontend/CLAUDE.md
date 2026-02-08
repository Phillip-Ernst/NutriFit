# Claude Instructions for NutriFit Frontend (React + Vite)

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Project Overview

NutriFit Frontend is a React + TypeScript single-page application built with Vite.
It provides authentication (JWT), meal logging, and dashboard views that consume
the Spring Boot backend API.

Primary goals:
- Keep UI simple, consistent, and responsive
- Maintain strict alignment with backend DTOs/contracts
- Add tests alongside changes (unit + component tests)

---

## Build & Run Commands

```bash
# Install dependencies
npm install

# Run (development)
npm run dev

# Build for production
npm run build

# Type-check without emitting
npx tsc --noEmit

# Preview production build
npm run preview

# Lint
npm run lint
```

---

### Environment Setup
* Vite dev server: http://localhost:5173
* Backend API expected at: http://localhost:8080/api (configured in src/api/axios.ts)
No .env file required for development.

To override the API base URL, set VITE_API_URL in a .env.local file:
```bash
VITE_API_URL=http://localhost:8080/api
```
Rules:
* Never commit secrets to git
* Prefer .env.local for machine-specific overrides

---

### Architecture
Stack: React 18, TypeScript, Vite, Tailwind CSS v4, React Router v6, TanStack Query, Axios.
#### Source structure `(src/)`:
* **api/** — Axios instance (`axios.ts` with JWT interceptor + 401 handler), endpoint modules (`auth.ts`, `meals.ts`).
* **components/ui/ — Reusable primitives**: `Button`, `Input`, `Card`, `StatCard`, `MacroBar`, `LoadingSpinner`, `Modal`, `ErrorBoundary`.
* **components/layout/** — `Navbar` (responsive with mobile hamburger) and `AppLayout` (navbar + `<Outlet />`).
* **components/meals/** — Domain components: `MealForm`, `FoodItemRow`, `MealCard`, `MealTable`, `NutritionSummary`.
* **context/** — `AuthContext` manages JWT token + username in localStorage with `login`, `register`, `logout`.
* **hooks/** — `useAuth` and `useMeals` (TanStack Query hooks: `useMyMeals`, `useCreateMeal`).
* **pages/** — One file per route: `LandingPage`, `LoginPage`, `RegisterPage`, `DashboardPage`, `MealLogPage`, `MealHistoryPage`, `WorkoutsPage`, `ProfilePage`, `NotFoundPage`.
* **routes/** — `AppRouter` and `ProtectedRoute` (redirects to `/login` if unauthenticated).
* **types/** — All TypeScript interfaces in `index.ts`.

#### Authentication
* JWT stored in `localStorage` under keys `token` and `username`
* Axios request interceptor attaches: `Authorization: Bearer <token>`
* Axios response interceptor:
  * clears storage
  * redirects to `/login` on 401/403
* Register auto-logs in after account creation

#### Styling
* Tailwind CSS v4 via Vite plugin
* Dark theme with `bg-gray-950` base and `text-emerald-400` accents
* No custom Tailwind config file — CSS-based config via `@import "tailwindcss"` in `index.css`

#### State Management
* React Context: auth state
* TanStack Query: server state (meals)
* No Redux or other global store

---

### Routes
| Path | Page            | Auth
|---|-----------------|---
|`/` | LandingPage     | Public|
| `/login`| LoginPage       | Public (redirects if authed) |
|`/register`	| RegisterPage    | Public (redirects if authed)
|`/dashboard`	| DashboardPage   | Protected
|`/meals/log`	| MealLogPage     |Protected
|`/meals/history`	| MealHistoryPage |	Protected
|`/workouts`	| WorkoutsPage	   |Protected
|`/profile`	| ProfilePage	    |Protected
|`*`	| NotFoundPage    |	Public|

---

### Conventions
* Functional components with default exports
* UI components accept `className` prop for Tailwind overrides
* API functions return unwrapped `response.data` (not the Axios response)
* Login endpoint returns plain-text JWT (not JSON) — handled via `transformResponse` override in `api/auth.ts`
* Nullable backend Integer fields mapped to `number | null` in TypeScript:
  * For display: use `val ?? 0`
* TanStack Query key for meals: `['meals', 'mine']`
  * Mutations invalidate this key on success

---

### Testing 
#### Testing Frameworks
* Vitest for unit tests
* React Testing Library for component tests
* MSW (Mock Service Worker) for API/network mocking when testing hooks/components that call Axios
* Avoid hitting the real backend in tests

If the repo does not yet have Vitest/RTL/MSW installed/configured, Claude should add the minimal setup
(dependencies + config + example tests) as part of the change that introduces testing.

#### Mandatory Rule: Add/Update Tests with Every Change
When creating or modifying any frontend file, Claude must also create or update
corresponding tests unless the change is purely formatting/comments.

#### Test Location & Naming

Preferred structure:
```bash
src/
  ...
  __tests__/                   # optional centralized tests
  components/.../*.test.tsx     # colocated tests are fine
  hooks/.../*.test.ts
  api/.../*.test.ts
```

#### Naming:
* Component tests: `{ComponentName}.test.tsx`
* Hook tests: `{hookName}.test.ts`
* API module tests: `{module}.test.ts`

#### What to Test (by area)
#### Components (Highest Priority)
* Rendering and interaction:
  * form input changes
  * button clicks
  * conditional UI states (loading/error/empty)
* Accessibility-friendly queries:
  * `getByRole`, `getByLabelText`, `findByText`, etc.

#### Hooks
* `useMeals` hooks:
  * successful fetch states
  * error handling states
  * mutation invalidates `['meals', 'mine']`
* Auth hooks/context:
  * login/register sets storage + state
  * logout clears storage + state

#### API Layer
* `api/auth.ts`:
  * handles plain-text token response correctly
  * attaches headers correctly via axios instance
* `api/meals.ts`:
  * returns `response.data` only
  * handles expected DTO shapes

#### Network Mocking Rules (MSW)

When testing hooks/components that make HTTP requests:
* Use MSW handlers to mock `/api/*` endpoints
* Do not stub Axios with brittle mocks unless necessary
* Tests must be deterministic and not depend on timing flakiness

#### Example Testing Patterns
#### Component test (RTL)

```ts
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import LoginPage from "../pages/LoginPage";

it("submits login form", async () => {
  render(<LoginPage />);
  await userEvent.type(screen.getByLabelText(/username/i), "phil");
  await userEvent.type(screen.getByLabelText(/password/i), "secret");
  await userEvent.click(screen.getByRole("button", { name: /log in/i }));
  // assert expected behavior (e.g., redirect call, token set, etc.)
});
```

#### Hook test (TanStack Query)
* Wrap hooks in a QueryClientProvider with a fresh QueryClient per test
* Prefer MSW for network behavior

#### Coverage Expectations
* Every new page/component should have at least:
  * one happy path test
  * one edge case test (empty state, validation error, etc.)
* Keep tests fast and reliable

> ✅ **Test coverage:** 16 test files with 141 tests covering:
> - Auth context and hooks
> - Meal form submission flow
> - API modules (auth, meals, axios interceptors)
> - UI components (Button, Input, Card, Modal, ErrorBoundary, etc.)
> - Page components (LoginPage, RegisterPage, DashboardPage, MealLogPage, etc.)

---

### API Contract Rules (Critical)

1. Match backend DTOs exactly
   * Field names, types, nesting

2. Do not transform API shapes silently
   * If transformation is required, do it in a dedicated mapper function and test it

3. Auth headers
   * Never manually add `Authorization` in every call if the interceptor already handles it

4. 401/403 behavior
   * Must clear `localStorage` and redirect to `/login` (covered by tests)

---

### Error Handling & UX Rules
* Loading states must show `LoadingSpinner` (or equivalent)
* Error states should render a friendly message and not crash the app
* Empty states should be explicit (e.g., "No meals yet")
* Avoid `alert()`; use UI messages/modals if needed

> ✅ **Error boundaries implemented** at `src/components/ui/ErrorBoundary.tsx`.
> All protected routes are wrapped with error boundaries in `AppRouter.tsx`.

---

## Critical Patterns to Follow

### Error Boundaries
Error boundaries are implemented at `src/components/ui/ErrorBoundary.tsx` and integrated into `AppRouter.tsx`.

The `ErrorBoundary` component:
- Catches JavaScript errors in child component tree
- Displays a styled fallback UI with "Try again" button
- Accepts optional custom `fallback` prop for custom error UI

All protected routes are wrapped with error boundaries in `AppRouter.tsx`:
```tsx
<Route path="/dashboard" element={
  <ErrorBoundary>
    <DashboardPage />
  </ErrorBoundary>
} />
```

### Auth Token Validation
The auth context should validate token expiry on app load:

```tsx
// In AuthContext initialization
useEffect(() => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload.exp * 1000 < Date.now()) {
        // Token expired, clear and redirect
        logout();
      }
    } catch {
      logout();
    }
  }
}, []);
```

### Accessibility Requirements
- All interactive elements need accessible labels
- Modal components must trap focus
- Icon-only buttons need `aria-label`

```tsx
// ❌ WRONG: No accessible label
<button onClick={onClose}>
  <XIcon />
</button>

// ✅ CORRECT: Has aria-label
<button onClick={onClose} aria-label="Close modal">
  <XIcon />
</button>
```

### User Feedback on Auth Errors
Currently, 401 redirects happen silently. Consider adding user notification:

```tsx
// In axios response interceptor
if (error.response?.status === 401) {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  // TODO: Show toast notification before redirect
  window.location.href = '/login';
}
```

---

### Guidelines for Code Changes
#### When Adding New Features

1. Add/update types in `src/types/index.ts`

2. Add/update API module in `src/api/`

3. Add/update hooks in `src/hooks/` (TanStack Query)

4. Build UI components in `src/components/`

5. Add page + route in `src/pages/` and `src/routes/`

6. Add/Update tests (Vitest + RTL + MSW)

#### “Don’t surprise me” rules
* Don’t introduce new state libraries unless asked
* Don’t add large UI frameworks unless asked
* Keep diffs small and focused
* Follow existing file/folder naming patterns

#### If uncertain
Ask whether the user wants:
* a quick patch
* or a proper refactor

Prefer patterns already used in the repo.

---
