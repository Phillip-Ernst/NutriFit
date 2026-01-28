# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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

## Environment Setup

Vite dev server runs on `http://localhost:5173`. Backend API expected at `http://localhost:8080/api` (configured in `src/api/axios.ts`).

No `.env` file required for development. To override the API base URL, set `VITE_API_URL` in a `.env.local` file.

## Architecture

**Stack:** React 18, TypeScript, Vite, Tailwind CSS v4, React Router v6, TanStack Query, Axios.

**Source structure** (`src/`):

- **api/** — Axios instance (`axios.ts` with JWT interceptor and 401 handler), and endpoint modules (`auth.ts`, `meals.ts`).
- **components/ui/** — Reusable primitives: `Button`, `Input`, `Card`, `StatCard`, `MacroBar`, `LoadingSpinner`, `Modal`.
- **components/layout/** — `Navbar` (responsive with mobile hamburger) and `AppLayout` (navbar + `<Outlet />`).
- **components/meals/** — Domain components: `MealForm`, `FoodItemRow`, `MealCard`, `MealTable`, `NutritionSummary`.
- **context/** — `AuthContext` manages JWT token and username in localStorage with `login`, `register`, `logout` functions.
- **hooks/** — `useAuth` (context convenience wrapper) and `useMeals` (TanStack Query hooks: `useMyMeals`, `useCreateMeal`).
- **pages/** — One file per route: `LandingPage`, `LoginPage`, `RegisterPage`, `DashboardPage`, `MealLogPage`, `MealHistoryPage`, `WorkoutsPage`, `ProfilePage`, `NotFoundPage`.
- **routes/** — `AppRouter` (route definitions) and `ProtectedRoute` (redirects to `/login` if unauthenticated).
- **types/** — All TypeScript interfaces in `index.ts`.

**Authentication:** JWT stored in `localStorage` under keys `token` and `username`. Axios request interceptor attaches `Authorization: Bearer <token>`. Response interceptor clears storage and redirects to `/login` on 401/403. Register auto-logs in after account creation.

**Styling:** Tailwind CSS v4 via Vite plugin. Dark theme with `bg-gray-950` base and `text-emerald-400` accents. No custom config file — uses CSS-based configuration via `@import "tailwindcss"` in `index.css`.

**State management:** React Context for auth state, TanStack Query for server state (meals). No Redux or other global store.

## Routes

| Path | Page | Auth |
|------|------|------|
| `/` | LandingPage | Public |
| `/login` | LoginPage | Public (redirects if authed) |
| `/register` | RegisterPage | Public (redirects if authed) |
| `/dashboard` | DashboardPage | Protected |
| `/meals/log` | MealLogPage | Protected |
| `/meals/history` | MealHistoryPage | Protected |
| `/workouts` | WorkoutsPage | Protected |
| `/profile` | ProfilePage | Protected |

## Conventions

- Functional components with default exports.
- UI components accept `className` prop for Tailwind overrides.
- API functions return unwrapped `response.data` (not the Axios response).
- Login endpoint returns plain-text JWT (not JSON) — handled via `transformResponse` override in `api/auth.ts`.
- Nullable backend `Integer` fields mapped to `number | null` in TypeScript — use `val ?? 0` for display.
- TanStack Query key for meals: `['meals', 'mine']`. Mutations invalidate this key on success.
