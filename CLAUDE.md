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
- [ ] **JWT secret generated in-memory** — `backend/.../service/JwtService.java` regenerates secret on every restart, invalidating all tokens. Move to environment variable.
- [ ] **CORS accepts all origins** — `backend/.../config/WebConfig.java` reflects any Origin header. Add explicit origin allowlist.
- [ ] **No database migrations** — Using `ddl-auto=update` risks data loss. Implement Flyway or Liquibase.
- [ ] **No global exception handler** — Validation errors return raw Spring HTML. Add `@RestControllerAdvice`.

### High Priority
- [ ] **Frontend missing error boundaries** — Single component error crashes entire React app.
- [ ] **Test coverage incomplete** — ~30% backend service coverage, ~10% frontend file coverage.
- [ ] **.env file committed** — Root `.env` contains DB credentials. Remove from git history.

### Medium Priority
- [ ] Spring Security dependency commented out in `pom.xml`
- [ ] Login endpoint returns plain text on failure (inconsistent with JSON API)
- [ ] Numeric DTO fields allow null without validation
- [ ] No controller tests (MockMvc) in backend
- [ ] Auth context doesn't validate token expiry on app load
- [ ] No structured logging configuration

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
