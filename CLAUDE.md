# NutriFit (monorepo) — Claude Code instructions

## What this repo is
NutriFit is a nutrition + fitness tracking app.
- backend/: Java Spring Boot API (JPA/Hibernate) + PostgreSQL
- frontend/: React app (Vite)

Primary goals:
- Clean REST API design, secure auth (JWT), and reliable DB modeling
- Simple, consistent React UI that matches API contracts

## Repo layout
- backend/ ... Spring Boot project (Maven)
- frontend/ ... React (Vite)

## How to run (dev)
### Backend
- From backend/:
    - ./mvnw spring-boot:run
    - ./mvnw test

### Frontend
- From frontend/:
    - npm install
    - npm run dev
    - npm run build
    - npm test

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

## Remaining Tech Debt (Low Priority)

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
