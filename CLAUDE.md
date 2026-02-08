# NutriFit (monorepo) — Claude Code instructions

## What this repo is
NutriFit is a nutrition + fitness tracking app.
- backend/: Java Spring Boot API (JPA/Hibernate) + PostgreSQL
- frontend/: React app (Vite)

Primary goals:
- Clean REST API design, secure auth (JWT), and reliable DB modeling
- Simple, consistent React UI that matches API contracts

## Repo layout
- backend/NutriFit/ ... Spring Boot project (Maven)
- frontend/ ... React (Vite)

## How to run (dev)
### Backend
- From backend/NutriFit:
    - mvn spring-boot:run
    - mvn test

### Frontend
- From frontend:
    - npm install
    - npm run dev
    - npm run build
    - npm test (if configured)

## Environment & secrets rules
- Never hardcode secrets (JWT secrets, DB passwords, API keys).
- Prefer .env (frontend) and environment variables/application-*.properties (backend).
- If you need new config values, add them to example env docs, not real secrets.

## Coding workflow expectations
- Make minimal, focused changes
- Keep diffs small and easy to review
- Update or add tests when behavior changes
- If you change API contracts, update frontend calls + DTOs consistently

## “Don’t surprise me” rules
- Don’t rename packages or folders broadly unless asked
- Don’t introduce new frameworks unless needed
- Don’t add heavy dependencies when a small solution works

## When uncertain
- Ask: "Do you want this as a quick patch or a proper refactor?"
- Prefer the existing patterns already used in the repo.

---

## Known Architecture Issues (To Address)

### Critical (Must Fix Before Production)
- [x] **JWT secret externalized** — JWT secret now loaded from `JWT_SECRET` environment variable (falls back to generated secret for dev only).
- [x] **CORS configured with allowlist** — `WebConfig.java` now uses explicit origin allowlist instead of reflecting any Origin header.
- [ ] **No database migrations** — Using `ddl-auto=update` risks data loss. Implement Flyway or Liquibase.
- [x] **Global exception handler added** — `GlobalExceptionHandler` with `@RestControllerAdvice` returns structured JSON errors.

### High Priority
- [x] **Frontend error boundaries implemented** — `ErrorBoundary` component at `src/components/ui/ErrorBoundary.tsx`, wrapping all routes in `AppRouter.tsx`.
- [x] **Test coverage improved** — Backend: ~80% controller coverage with MockMvc tests. Frontend: 16 test files covering components, hooks, API, and pages.
- [x] **.env file committed** — Verified `.env` was never committed; `.gitignore` properly configured.
- [x] **N+1 query in WorkoutPlanServiceImpl** — Fixed with `@EntityGraph(attributePaths = {"days", "days.exercises"})` on repository methods.
- [x] **Missing numeric validation on DTOs** — Added `@Min(0)` validation to `FoodItemRequest`, `ExerciseItemRequest`, `WorkoutPlanExerciseRequest`, and `WorkoutPlanDayRequest`.

### Medium Priority
- [x] Spring Security dependency enabled in `pom.xml`
- [ ] Login endpoint returns plain text on failure (inconsistent with JSON API)
- [x] Controller tests (MockMvc) added for all controllers (MealLog, WorkoutLog, WorkoutPlan, User, Exercise, Health)
- [x] Auth context validates token expiry on app load (`isTokenExpired()` in `AuthContext.tsx`)
- [x] Structured logging added via `RequestLoggingFilter` for request/response logging
- [ ] **DTO reuse anti-pattern** — `MealLogResponse` uses `FoodItemRequest` instead of separate response DTO
- [ ] **Unused OAuth2 dependency** — `spring-boot-starter-oauth2-client` in pom.xml but not used
- [ ] **No OpenAPI documentation** — Missing Springdoc annotations for auto-generated API docs

### Low Priority (Tech Debt)
- [ ] **Frontend accessibility gaps** — Icon-only buttons missing `aria-label` in Modal, Navbar, FoodItemRow
- [ ] **Modal focus not trapped** — Modal component doesn't implement keyboard focus trap
- [ ] **Array index as React key** — Several components use index as key (MealForm, WorkoutForm, MealCard, etc.)
- [ ] **No axios interceptor tests** — Critical auth flow in `axios.ts` lacks test coverage
- [ ] **Silent 401 redirect** — Auth failure redirects without user notification (no toast/message)

---

## Security Guidelines

### JWT Configuration
- `JWT_SECRET` **must** be externalized via environment variable
- Never generate secrets in constructors or static initializers
- Document key rotation strategy for production

### CORS Configuration
- CORS origins **must** be explicitly whitelisted
- Never reflect arbitrary `Origin` headers back to the client
- Use Spring's `@CrossOrigin` with explicit `origins` or configure in `WebMvcConfigurer`

### Secrets Management
- Never commit `.env` files (use `.env.example` templates)
- Use environment variables or secret managers for production
- All numeric inputs should be validated (positive, within expected range)

---

## API Contract Rules

### Backend → Frontend Alignment
- Backend `Instant` fields serialize to ISO-8601 strings (e.g., `"2024-01-15T10:30:00Z"`)
- Response DTOs should be separate from Request DTOs (avoid reuse)
- Nullable numeric fields: use `number | null` in TypeScript, display with `val ?? 0`
- DateTime comparisons (e.g., `isToday()`) should use UTC to avoid timezone issues

### Error Response Format
All API errors should return consistent JSON:
```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable description",
  "timestamp": "2024-01-15T10:30:00Z"
}
```
