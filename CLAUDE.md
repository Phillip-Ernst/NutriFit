# NutriFit (monorepo) — Claude Code instructions

## What this repo is
NutriFit is a nutrition + fitness tracking app.
- backend/: Java Spring Boot API (JPA/Hibernate) + PostgreSQL
- frontend/: React app (Vite)

Primary goals:
- Clean REST API design, secure auth (JWT), and reliable DB modeling
- User profile and body measurement tracking
- Simple, consistent React UI that matches API contracts

## Repo layout
- backend/ ... Spring Boot project (Maven)
- frontend/ ... React (Vite)

---

## Database Architecture

### Environments
| Environment | Database | Purpose |
|-------------|----------|---------|
| **Local Dev** | Docker PostgreSQL | Fast iteration, isolated per developer |
| **CI/CD** | Docker PostgreSQL | Automated tests in GitHub Actions |
| **Production** | AWS RDS | Real user data, managed service |

### Flyway Migrations
- Migrations located in `backend/src/main/resources/db/migration/`
- Naming convention: `V{number}__{description}.sql` (e.g., `V4__add_uuid_to_workout_plan_day.sql`)
- Production uses `ddl-auto: validate` — schema changes MUST go through Flyway
- Local can use `ddl-auto: update` for rapid iteration, but always create migrations before merging

### Current Migrations
- `V1__initial_schema.sql` — Users, meals, foods
- `V2__add_user_profile.sql` — Profile and measurements
- `V3__add_workouts.sql` — Workouts, exercises, workout plans
- `V4__add_uuid_to_workout_plan_day.sql` — UUID column for entity equality

---

## How to Run Locally

### 1. Start the Database (Docker)
```bash
cd backend
docker compose up -d
```
PostgreSQL runs on port 5432.

### 2. Run the Backend
```bash
cd backend

# Load environment variables
source NutriFit-backend.env

# Run with Maven
./mvnw spring-boot:run
```
Backend runs at `http://localhost:8080/api`

### 3. Run the Frontend
```bash
cd frontend
npm install   # first time only
npm run dev
```
Frontend runs at `http://localhost:5173`

### Quick Reference
| Task | Command | Location |
|------|---------|----------|
| Start DB | `docker compose up -d` | backend/ |
| Stop DB | `docker compose down` | backend/ |
| Reset DB | `docker compose down -v && docker compose up -d` | backend/ |
| Run backend | `./mvnw spring-boot:run` | backend/ |
| Run frontend | `npm run dev` | frontend/ |
| Backend tests | `./mvnw test` | backend/ |
| Frontend tests | `npm test` | frontend/ |
| Flyway migrate | `./mvnw flyway:migrate` | backend/ |
| Flyway repair | `./mvnw flyway:repair` | backend/ |

### Troubleshooting
**Port 8080 in use:**
```bash
lsof -ti:8080 | xargs kill -9
```

**Database won't start:**
```bash
docker compose down -v && docker compose up -d
```

**Flyway migration fails:**
```bash
./mvnw flyway:repair
./mvnw flyway:migrate
```

---

## Environment & secrets rules
- Never hardcode secrets (JWT secrets, DB passwords, API keys).
- Prefer .env (frontend) and environment variables (backend).
- Backend env vars stored in `backend/NutriFit-backend.env` (not committed).
- Required backend env vars: `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, `SPRING_DATASOURCE_*`
- If you need new config values, add them to example env docs, not real secrets.

---

## Backend JPA/Hibernate Patterns

### Entity Collections: Use Set, Not List
When an entity has multiple `@OneToMany` or `@ElementCollection` relationships that may be fetched together (via `@EntityGraph` or eager loading), use `Set` instead of `List` to avoid `MultipleBagFetchException`.

```java
// CORRECT: Use Set with LinkedHashSet for ordering
@Builder.Default
@OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<WorkoutPlanDay> days = new LinkedHashSet<>();

// WRONG: List causes MultipleBagFetchException with multiple eager fetches
private List<WorkoutPlanDay> days = new ArrayList<>();
```

### Entity Equality: Use UUID for Set Collections
Entities stored in `Set` collections need proper `equals()`/`hashCode()` before persistence (when `id` is still `null`). Use a UUID field:

```java
@Builder.Default
@Column(nullable = false, updatable = false, unique = true)
private String uuid = UUID.randomUUID().toString();

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WorkoutPlanDay that = (WorkoutPlanDay) o;
    return Objects.equals(uuid, that.uuid);
}

@Override
public int hashCode() {
    return Objects.hash(uuid);
}
```

**Why:** Without UUID-based equality, new entities with `id = null` are all considered equal, causing Set deduplication bugs.

### Entities Using These Patterns
- `WorkoutPlan` — `days` is a `Set<WorkoutPlanDay>`
- `WorkoutPlanDay` — `exercises` is a `Set<WorkoutPlanExercise>`, uses UUID equality

---

## Coding workflow expectations
- Make minimal, focused changes
- Keep diffs small and easy to review
- Update or add tests when behavior changes
- If you change API contracts, update frontend calls + DTOs consistently

## "Don't surprise me" rules
- Don't rename packages or folders broadly unless asked
- Don't introduce new frameworks unless needed
- Don't add heavy dependencies when a small solution works

## When uncertain
- Ask: "Do you want this as a quick patch or a proper refactor?"
- Prefer the existing patterns already used in the repo.

---

## Remaining Tech Debt (Low Priority)

- [ ] **Frontend accessibility gaps** — Icon-only buttons missing `aria-label` in Modal, Navbar, FoodItemRow
- [ ] **Modal focus not trapped** — Modal component doesn't implement keyboard focus trap
- [ ] **Array index as React key** — Several components use index as key (MealForm, WorkoutForm, MealCard, etc.)
- [ ] **No axios interceptor tests** — Critical auth flow in `axios.ts` lacks test coverage
- [ ] **Silent 401 redirect on protected routes** — 401 from protected routes redirects without notification (auth pages now show API error messages)

## Recently Fixed Issues

- [x] **MultipleBagFetchException** — Fixed by changing `List` to `Set` in `WorkoutPlan.days` and `WorkoutPlanDay.exercises`
- [x] **Duplicate days in workout plans** — Fixed by Set collections deduplicating Cartesian product from JPA joins
- [x] **Only Day 1 being added to plans** — Fixed by adding UUID-based equality to `WorkoutPlanDay` (V4 migration)

---

## Security Guidelines

### JWT Configuration
- `JWT_SECRET` **must** be externalized via environment variable
- Never generate secrets in constructors or static initializers
- Document key rotation strategy for production

### CORS Configuration
- CORS origins **must** be explicitly whitelisted via `CORS_ALLOWED_ORIGINS` env var
- Never reflect arbitrary `Origin` headers back to the client
- CORS is configured in `SecurityConfig.java` using Spring Security's `CorsConfigurationSource`
- Supports comma-separated origins (e.g., `http://localhost:5173,https://example.com`)

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
