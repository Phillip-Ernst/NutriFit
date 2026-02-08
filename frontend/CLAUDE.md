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
* **api/** — Axios instance (`axios.ts` with JWT interceptor + 401 handler), endpoint modules (`auth.ts`, `meals.ts`, `workouts.ts`, `workoutPlans.ts`).
* **components/ui/** — Reusable primitives: `Button`, `Input`, `Card`, `StatCard`, `MacroBar`, `LoadingSpinner`, `Modal`, `ErrorBoundary`.
* **components/layout/** — `Navbar` (responsive with mobile hamburger) and `AppLayout` (navbar + `<Outlet />`).
* **components/meals/** — Domain components: `MealForm`, `FoodItemRow`, `MealCard`, `MealTable`, `NutritionSummary`.
* **components/workouts/** — Workout logging: `WorkoutForm`, `ExerciseItemRow`, `WorkoutCard`.
* **components/workoutPlans/** — Workout plan management: `WorkoutPlanForm`, `WorkoutPlanCard`, `WorkoutPlanDayForm`, `PlanExerciseRow`, `ExecuteWorkoutForm`.
* **context/** — `AuthContext` manages JWT token + username in localStorage with `login`, `register`, `logout`, and token expiry validation.
* **hooks/** — `useAuth`, `useMeals` (TanStack Query hooks), `useWorkouts`, `useWorkoutPlans`.
* **pages/** — One file per route (see Routes table below).
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
| Path | Page | Auth |
|------|------|------|
| `/` | LandingPage | Public |
| `/login` | LoginPage | Public (redirects if authed) |
| `/register` | RegisterPage | Public (redirects if authed) |
| `/dashboard` | DashboardPage | Protected |
| `/meals/log` | MealLogPage | Protected |
| `/meals/history` | MealHistoryPage | Protected |
| `/workouts` | WorkoutsPage | Protected |
| `/workouts/log` | WorkoutLogPage | Protected |
| `/workouts/history` | WorkoutHistoryPage | Protected |
| `/workouts/plans` | WorkoutPlansPage | Protected |
| `/workouts/plans/new` | CreatePlanPage | Protected |
| `/workouts/plans/:id` | PlanDetailPage | Protected |
| `/workouts/plans/:id/edit` | EditPlanPage | Protected |
| `/workouts/execute/:dayId` | ExecuteWorkoutPage | Protected |
| `/profile` | ProfilePage | Protected |
| `*` | NotFoundPage | Public |

---

### Conventions
* Functional components with default exports
* UI components accept `className` prop for Tailwind overrides
* API functions return unwrapped `response.data` (not the Axios response)
* Login endpoint returns JSON `{"token": "..."}` — `api/auth.ts` extracts the token from `response.data.token`
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
  * handles JSON `{"token": "..."}` response, extracting `response.data.token`
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

> ✅ **Test coverage:** 16 test files covering:
> - Auth context and hooks (`AuthContext.test.ts`)
> - Meal components and hooks (`MealForm.test.tsx`, `useMeals.test.tsx`)
> - Workout components and hooks (`WorkoutForm.test.tsx`, `WorkoutCard.test.tsx`, `useWorkouts.test.tsx`)
> - Workout plan components and hooks (`WorkoutPlanCard.test.tsx`, `useWorkoutPlans.test.tsx`)
> - API modules (`auth.test.ts`, `meals.test.ts`, `workouts.test.ts`, `workoutPlans.test.ts`)
> - UI components (`ErrorBoundary.test.tsx`)
> - Page components (`LoginPage.test.tsx`, `RegisterPage.test.tsx`)
> - Routes (`ProtectedRoute.test.tsx`)
>
> **Gap:** `axios.ts` interceptors lack direct test coverage.

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
> All routes are wrapped with a parent-level error boundary in `AppRouter.tsx`.

---

## Critical Patterns to Follow

### Error Boundaries
Error boundaries are implemented at `src/components/ui/ErrorBoundary.tsx` and integrated into `AppRouter.tsx`.

The `ErrorBoundary` component:
- Catches JavaScript errors in child component tree
- Displays a styled fallback UI with "Try again" button
- Accepts optional custom `fallback` prop for custom error UI

All routes are wrapped with a parent-level error boundary in `AppRouter.tsx`:
```tsx
export default function AppRouter() {
  return (
    <ErrorBoundary>
      <Routes>
        {/* All routes are protected by this single boundary */}
      </Routes>
    </ErrorBoundary>
  );
}
```

### Auth Token Validation
> ✅ **Implemented** — Token expiry validation is in `AuthContext.tsx` via the `isTokenExpired()` function.

The auth context validates token expiry on app load using the exported `isTokenExpired()` helper:

```tsx
// In AuthContext.tsx
export function isTokenExpired(token: string): boolean {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return true;
    const payload = JSON.parse(atob(parts[1]));
    if (!payload.exp) return true;
    // Add 10s buffer for clock skew
    return Date.now() >= payload.exp * 1000 - 10000;
  } catch {
    return true;
  }
}
```

On initialization, expired tokens are cleared from localStorage automatically.

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

## Known Issues & Tech Debt

### Anti-Patterns to Avoid/Fix

#### Array Index as Key (HIGH)
Several components use array index as React key, which causes bugs when items are reordered/removed:

**Affected files:**
- `MealForm.tsx` — `foods.map((food, i) => <FoodItemRow key={i} ...`
- `WorkoutForm.tsx` — `exercises.map((exercise, i) => ...`
- `MealCard.tsx` — `meal.foods.map((food, i) => ...`
- `WorkoutPlanDayForm.tsx` — `day.exercises.map((exercise, i) => ...`
- `ExecuteWorkoutForm.tsx` — `exercises.map((exercise, i) => ...`

**Fix:** Generate unique IDs (e.g., `crypto.randomUUID()`) when adding items.

### Accessibility Gaps (MEDIUM)

#### Missing ARIA Labels on Icon Buttons
- `Modal.tsx` close button (line 22-26)
- `Navbar.tsx` mobile menu toggle (line 63-74)
- `FoodItemRow.tsx` remove button (line 57-65)

**Fix:** Add `aria-label` to all icon-only buttons.

#### Modal Focus Not Trapped
`Modal.tsx` doesn't trap keyboard focus, allowing Tab to escape the modal.

**Fix:** Implement focus trap using `useRef` or a library like `focus-trap-react`.

### Missing Tests (MEDIUM)
- `axios.ts` interceptors — JWT injection and 401/403 handling untested
- Complex form validation logic in `WorkoutPlanForm.tsx`, `WorkoutPlanDayForm.tsx`

### Performance Opportunities (LOW)
- Consider `useMemo` for derived calculations in `WorkoutsPage.tsx` (totalMinutes, totalCalories)
- No pagination for meal/workout history — may need cursor-based pagination for large datasets

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
