# NutriFit (monorepo) — Claude Code instructions

## What this repo is
NutriFit is a nutrition + fitness tracking app built as a microservices architecture.
- `backend/`: Java Spring Boot API — auth, workouts, workout plans, profile, measurements
- `nutrition-service/`: Java Spring Boot API — meal logging (extracted microservice)
- `frontend/`: React app (Vite)
- `nginx/`: nginx config for production routing and local Docker stack

Primary goals:
- Clean REST API design, secure auth (JWT), and reliable DB modeling
- User profile and body measurement tracking
- Simple, consistent React UI that matches API contracts

## Repo layout
```
NutriFit/
├── backend/          Spring Boot — auth, workouts, plans, profile, measurements (port 8080)
├── nutrition-service/ Spring Boot — meal logging (port 8081)
├── frontend/         React (Vite) — single frontend for both services
├── nginx/            nginx routing config (production + local Docker)
└── scripts/          AWS setup, DB init scripts, task definitions
```

---

## Microservices Architecture

### Routing
All traffic goes through a single entry point — nginx (local Docker) or ALB (production):

| Path pattern | Routes to | Port |
|---|---|---|
| `/api/meals*` | nutrition-service | 8081 |
| `/api/*` | backend | 8080 |
| `/` (default) | frontend | 80/443 |

### JWT Trust Model
Both services share the same `JWT_SECRET`. The nutrition-service validates JWTs directly from claims — no database lookup, no `UserDetailsService`. The backend issues tokens on login; nutrition-service trusts them.

---

## Database Architecture

### Two Databases, One PostgreSQL Instance
| Database | Owner | Purpose |
|---|---|---|
| `nutrifit` | backend | users, workouts, plans, profile, measurements |
| `nutrifit_nutrition` | nutrition-service | meal_log, meal_log_foods |

### Environments
| Environment | Database | Purpose |
|---|---|---|
| **Local Dev** | Docker PostgreSQL | Fast iteration, isolated per developer |
| **CI/CD** | Testcontainers | Automated tests in GitHub Actions |
| **Production** | AWS RDS PostgreSQL | Real user data, managed service |

### Flyway Migrations

**Backend** (`backend/src/main/resources/db/migration/`):
- `V1__initial_schema.sql` — Users, workouts, workout plans
- `V2__add_profile_and_measurements.sql` — Profile and body measurements
- `V3__add_user_change_history.sql` — User change history audit table
- `V4__add_uuid_to_workout_plan_day.sql` — UUID for WorkoutPlanDay equality
- `V5__add_per_set_tracking.sql` — Per-set weight/reps tracking
- `V5__drop_meal_tables.sql` — Drops legacy meal tables (run after nutrition-service migration)

**Nutrition-service** (`nutrition-service/src/main/resources/db/migration/`):
- `V1__create_meal_tables.sql` — meal_log, meal_log_foods (uses `username` string, no FK to users)

---

## How to Run Locally

### 1. Start the Database (Docker)
```bash
cd backend
docker compose up -d
```
This starts PostgreSQL on port 5432 and creates **both** `nutrifit` and `nutrifit_nutrition` databases via `scripts/init-multiple-dbs.sh`.

### 2. Run the Backend
```bash
cd backend
source NutriFit-backend.env
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
Backend runs at `http://localhost:8080/api`

### 3. Run the Nutrition Service
```bash
cd nutrition-service
source ../backend/NutriFit-backend.env   # shares the same env file
./mvnw spring-boot:run
```
Nutrition service runs at `http://localhost:8081/api`

### 4. Run the Frontend
```bash
cd frontend
npm install   # first time only
npm run dev
```
Frontend runs at `http://localhost:5173`

The Vite dev server proxies API calls:
- `/api/meals*` → `http://localhost:8081`
- `/api/*` → `http://localhost:8080`

### Quick Reference
| Task | Command | Location |
|------|---------|----------|
| Start DB | `docker compose up -d` | backend/ |
| Stop DB | `docker compose down` | backend/ |
| Reset DB | `docker compose down -v && docker compose up -d` | backend/ |
| Run backend | `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` | backend/ |
| Run nutrition-service | `./mvnw spring-boot:run` | nutrition-service/ |
| Run frontend | `npm run dev` | frontend/ |
| Backend tests | `./mvnw test` | backend/ |
| Nutrition tests | `./mvnw test` | nutrition-service/ |
| Frontend tests | `npm test` | frontend/ |

### Troubleshooting
**Kill services on ports:**
```bash
lsof -ti:8080,8081 | xargs kill -9
```

**Database won't start / nutrifit_nutrition missing:**
```bash
docker compose down -v && docker compose up -d
```
The `-v` flag removes the volume so the init script re-runs and recreates both databases.

**Tests fail due to env vars:**
Run `./mvnw test` without sourcing the `.env` file — tests use Testcontainers and set their own datasource URL.

---

## Environment & Secrets Rules
- Never hardcode secrets (JWT secrets, DB passwords, API keys).
- Backend and nutrition-service share `backend/NutriFit-backend.env` (not committed).
- Required env vars: `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, `SPRING_DATASOURCE_*`
- If you need new config values, add them to example env docs, not real secrets.

---

## Backend JPA/Hibernate Patterns

### Entity Collections: Use Set, Not List
When an entity has multiple `@OneToMany` or `@ElementCollection` relationships that may be fetched together, use `Set` instead of `List` to avoid `MultipleBagFetchException`.

```java
// CORRECT: Use Set with LinkedHashSet for ordering
@Builder.Default
@OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<WorkoutPlanDay> days = new LinkedHashSet<>();
```

### Entity Equality: Use UUID for Set Collections
Entities stored in `Set` collections need proper `equals()`/`hashCode()` before persistence. Use a UUID field:

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
```

### Entities Using These Patterns
- `WorkoutPlan` — `days` is a `Set<WorkoutPlanDay>`
- `WorkoutPlanDay` — `exercises` is a `Set<WorkoutPlanExercise>`, uses UUID equality

---

## Coding Workflow Expectations
- Make minimal, focused changes
- Keep diffs small and easy to review
- Update or add tests when behavior changes
- If you change API contracts, update frontend calls + DTOs consistently
- Meal endpoints belong to nutrition-service — never add meal logic to backend

## "Don't Surprise Me" Rules
- Don't rename packages or folders broadly unless asked
- Don't introduce new frameworks unless needed
- Don't add heavy dependencies when a small solution works

## When Uncertain
- Ask: "Do you want this as a quick patch or a proper refactor?"
- Prefer the existing patterns already used in the repo.

---

## Remaining Tech Debt (Low Priority)

- [ ] **Frontend accessibility gaps** — Icon-only buttons missing `aria-label` in Modal, Navbar, FoodItemRow
- [ ] **Modal focus not trapped** — Modal component doesn't implement keyboard focus trap
- [ ] **Array index as React key** — Several components use index as key (MealForm, WorkoutForm, MealCard, etc.)
- [ ] **No axios interceptor tests** — Critical auth flow in `axios.ts` lacks test coverage
- [ ] **Silent 401 redirect on protected routes** — 401 from protected routes redirects without notification
- [ ] **Duplicate V5 migration** — `V5__add_per_set_tracking.sql` and `V5__drop_meal_tables.sql` share the same version number; rename one before adding V6

---

## Security Guidelines

### JWT Configuration
- `JWT_SECRET` **must** be externalized via environment variable
- Both backend and nutrition-service must use the **same** secret (tokens are cross-service)
- Never generate secrets in constructors or static initializers

### CORS Configuration
- CORS origins **must** be explicitly whitelisted via `CORS_ALLOWED_ORIGINS` env var
- Never reflect arbitrary `Origin` headers back to the client
- Both services configure CORS independently via `SecurityConfig.java`

### Secrets Management
- Never commit `.env` files (use `.env.example` templates)
- Production secrets managed via AWS Secrets Manager
- All numeric inputs should be validated (positive, within expected range)

---

## API Contract Rules

### Backend → Frontend Alignment
- Backend `Instant` fields serialize to ISO-8601 strings (e.g., `"2024-01-15T10:30:00Z"`)
- Response DTOs should be separate from Request DTOs (avoid reuse)
- Nullable numeric fields: use `number | null` in TypeScript, display with `val ?? 0`
- DateTime comparisons (e.g., `isToday()`) should use UTC to avoid timezone issues

### Error Response Format
All API errors from both services return consistent JSON:
```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable description",
  "timestamp": "2024-01-15T10:30:00Z"
}
```