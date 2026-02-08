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
- [x] **Frontend error boundaries implemented** — `ErrorBoundary` component at `src/components/ui/ErrorBoundary.tsx`, integrated into `AppRouter.tsx`.
- [x] **Test coverage improved** — Backend: ~80% controller coverage with MockMvc tests. Frontend: 16 test files with 141 tests covering components, hooks, API, and pages.
- [ ] **.env file committed** — Root `.env` contains DB credentials. Remove from git history.

### Medium Priority
- [x] Spring Security dependency enabled in `pom.xml`
- [ ] Login endpoint returns plain text on failure (inconsistent with JSON API)
- [ ] Numeric DTO fields allow null without validation
- [x] Controller tests (MockMvc) added for all controllers (MealLog, WorkoutLog, WorkoutPlan, User, Exercise, Health)
- [ ] Auth context doesn't validate token expiry on app load
- [x] Structured logging added via `RequestLoggingFilter` for request/response logging

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

### Error Response Format
All API errors should return consistent JSON:
```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable description",
  "timestamp": "2024-01-15T10:30:00Z"
}
```
