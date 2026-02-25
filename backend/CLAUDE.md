# Claude Instructions for NutriFit Backend (Spring Boot API)

## Project Overview

NutriFit backend is a Java Spring Boot API handling **auth, workouts, workout plans, user profiles, and body measurements**.

Meal logging has been extracted to `nutrition-service/` (port 8081). Do NOT add meal logic here.

The backend is a Maven-based Spring Boot app with PostgreSQL persistence, stateless JWT auth, and a traditional layered package organization.

---

## Architecture Patterns

### 1. Traditional Layered Package Structure

All packages are under `src/main/java/com/phillipe/NutriFit/`

```
com/phillipe/NutriFit/
├── NutriFitApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── JacksonConfig.java
│   ├── RateLimitConfig.java
│   ├── GlobalExceptionHandler.java
│   └── filter/
│       ├── JwtFilter.java
│       ├── RateLimitFilter.java
│       └── RequestIdFilter.java
├── controller/
│   ├── UserController.java
│   ├── WorkoutLogController.java
│   ├── WorkoutPlanController.java
│   ├── ExerciseController.java
│   ├── HealthController.java
│   ├── ProfileController.java
│   └── MeasurementController.java
├── service/
│   ├── JwtService.java
│   ├── MyUserDetailsService.java
│   ├── UserService.java
│   ├── ChangeHistoryService.java
│   ├── WorkoutLogService.java
│   ├── WorkoutPlanService.java
│   ├── ProfileService.java
│   └── MeasurementService.java
│   └── impl/
│       ├── ChangeHistoryServiceImpl.java
│       ├── WorkoutLogServiceImpl.java
│       ├── WorkoutPlanServiceImpl.java
│       ├── ProfileServiceImpl.java
│       └── MeasurementServiceImpl.java
├── repository/
│   ├── UserRepository.java
│   ├── UserChangeHistoryRepository.java
│   ├── WorkoutLogRepository.java
│   ├── WorkoutPlanRepository.java
│   ├── WorkoutPlanDayRepository.java
│   ├── UserProfileRepository.java
│   └── BodyMeasurementRepository.java
├── model/
│   ├── ExerciseCategory.java        # Enum
│   ├── Gender.java                  # Enum
│   ├── PredefinedExercise.java      # Enum
│   ├── UnitPreference.java          # Enum
│   ├── entity/
│   │   ├── User.java
│   │   ├── UserProfile.java
│   │   ├── BodyMeasurement.java
│   │   ├── UserChangeHistory.java
│   │   ├── WorkoutLog.java
│   │   ├── WorkoutPlan.java
│   │   └── WorkoutPlanDay.java
│   └── embedded/
│       ├── WorkoutExerciseEntry.java
│       ├── WorkoutSetEntry.java
│       └── WorkoutPlanExercise.java
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── ExerciseItemRequest.java
│   │   ├── SetItemRequest.java
│   │   ├── WorkoutLogRequest.java
│   │   ├── WorkoutLogFromPlanRequest.java
│   │   ├── WorkoutPlanRequest.java
│   │   ├── WorkoutPlanDayRequest.java
│   │   ├── WorkoutPlanExerciseRequest.java
│   │   ├── ProfileUpdateRequest.java
│   │   └── MeasurementRequest.java
│   └── response/
│       ├── ErrorResponse.java
│       ├── LoginResponse.java
│       ├── UserResponse.java
│       ├── UserChangeHistoryResponse.java
│       ├── PredefinedExerciseResponse.java
│       ├── WorkoutLogResponse.java
│       ├── WorkoutPlanResponse.java
│       ├── WorkoutPlanDayResponse.java
│       ├── WorkoutPlanExerciseResponse.java
│       ├── ProfileResponse.java
│       └── MeasurementResponse.java
├── exception/
│   └── DuplicateUsernameException.java
└── security/
    └── UserPrincipal.java
```

### 2. Layered Responsibility

- **Model layer (`model/`)**: persistence entities (`entity/`), embedded types (`embedded/`), enums
- **DTO layer (`dto/`)**: request/response shapes (API boundary)
- **Service layer (`service/`)**: business logic; implementations in `service/impl/`
- **Controller layer (`controller/`)**: HTTP routing + auth principal extraction
- **Repository layer (`repository/`)**: Spring Data JPA repositories
- **Config layer (`config/`)**: Spring configuration, security, filters

---

## Build & Run Commands

```bash
# Build
./mvnw clean package

# Run (local dev — requires NutriFit-backend.env sourced)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Run all tests (do NOT source the env file before running tests)
./mvnw test

# Run a single test class
./mvnw test -Dtest=WorkoutLogControllerTest
```

---

## Environment & Configuration

### Spring Profiles

| Profile | Config File | Purpose |
|---------|-------------|---------|
| (base) | `application.yaml` | Common settings (server, JPA, Flyway, JWT, CORS) |
| `local` | `application-local.yaml` | Local dev — PostgreSQL at localhost:5432 |
| `docker` | `application-docker.yaml` | Full Docker stack — PostgreSQL at service name |
| `rds` | `application-rds.yaml` | Production — AWS RDS |
| (test) | `src/test/resources/application.yaml` | Testcontainers — auto-managed |

### Environment Variables

#### All Profiles (Required)
| Variable | Description |
|----------|-------------|
| `JWT_SECRET` | JWT signing key (min 32 chars for HS256) — must match nutrition-service |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins |

#### Profile: `local`
| Variable | Example |
|----------|---------|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `nutrifit` |
| `DB_USER` | `nutrifit` |
| `DB_PASSWORD` | `nutrifit` |

#### Profile: `rds` (Production)
Datasource configured via `SPRING_DATASOURCE_URL/USERNAME/PASSWORD` env vars (set in ECS task definition). No profile flag needed — Spring Boot picks up these standard env vars automatically.

### Server / API Base Path
* Server port: **8080**
* Base API path: `/api`

---

## Security & Authentication

### JWT (Stateless)
* Stateless JWT authentication
* Public endpoints: `/api/register` and `/api/login`
* All other endpoints require: `Authorization: Bearer <token>`
* Token expiry: **1 hour**
* Password hashing: **BCrypt strength 12**

### Spring Security Configuration
* Security config: `config/SecurityConfig.java`
* JWT filter: `config/filter/JwtFilter.java`
* CSRF disabled (API-only), session policy is stateless

---

## Database & ORM

### JPA/Hibernate
* `ddl-auto=validate` — schema must match entities exactly
* All schema changes go through Flyway migrations

### Flyway Migrations (`src/main/resources/db/migration/`)
- `V1__initial_schema.sql` — Users, workouts, workout plans
- `V2__add_profile_and_measurements.sql` — UserProfile, BodyMeasurement
- `V3__add_user_change_history.sql` — Audit trail for user changes
- `V4__add_uuid_to_workout_plan_day.sql` — UUID column for WorkoutPlanDay
- `V5__add_per_set_tracking.sql` — Per-set weight/reps on WorkoutLog
- `V5__drop_meal_tables.sql` — Drops legacy meal_log tables (⚠️ duplicate version — fix before adding V6)

### Entity Relationships
* User (1) → one UserProfile
* User (1) → many BodyMeasurement
* User (1) → many WorkoutLog
* User (1) → many WorkoutPlan
* WorkoutPlan (1) → many WorkoutPlanDay
* WorkoutPlanDay (1) → many WorkoutPlanExercise via `@ElementCollection`
* WorkoutLog (1) → many WorkoutExerciseEntry via `@ElementCollection`
* WorkoutExerciseEntry (1) → many WorkoutSetEntry via `@ElementCollection`

### Persistence Rules
* Prefer DTOs at the API boundary — don't return entities from controllers
* Use LAZY for `@ManyToOne` relationships
* Use `Set` (not `List`) for `@ElementCollection` and `@OneToMany` to avoid `MultipleBagFetchException`
* Use UUID-based `equals()`/`hashCode()` on entities stored in Sets
* Use `Instant` for timestamps (serializes to ISO-8601)

---

## API Endpoints

All endpoints are under `/api`:

**User/Auth:**
* `POST /register` — public, creates user
* `POST /login` — public, returns `{"token": "..."}`

**Workouts:**
* `POST /workouts` — authenticated, creates workout log
* `POST /workouts/from-plan` — authenticated, creates workout from a plan day
* `GET /workouts/mine` — authenticated, returns current user's workouts (newest first)

**Workout Plans:**
* `POST /workout-plans` — authenticated, creates plan
* `GET /workout-plans/mine` — authenticated, returns current user's plans
* `GET /workout-plans/{id}` — authenticated, returns specific plan
* `PUT /workout-plans/{id}` — authenticated, updates plan
* `DELETE /workout-plans/{id}` — authenticated, deletes plan
* `GET /workout-plans/days/{dayId}` — authenticated, returns specific day

**Exercises:**
* `GET /exercises/predefined` — predefined exercises (optional `?category=` filter)
* `GET /exercises/categories` — all exercise categories

**Profile:**
* `GET /profile` — authenticated, returns user profile (auto-creates if not exists)
* `PUT /profile` — authenticated, updates `birthYear`, `gender`, `unitPreference`

**Measurements:**
* `POST /measurements` — authenticated, creates body measurement
* `GET /measurements` — authenticated, returns all (newest first)
* `GET /measurements/latest` — authenticated, returns most recent
* `DELETE /measurements/{id}` — authenticated, deletes a measurement

> **Meals are NOT handled here.** See `nutrition-service/` for `/api/meals*` endpoints.

---

## Code Patterns & Conventions

### Lombok
* `@Data`, `@Builder`, `@Getter`, `@Setter` on models and DTOs
* Avoid Lombok patterns that interfere with JPA proxying

### Validation
Use Jakarta validation on request DTOs: `@NotEmpty`, `@NotBlank`, `@NotNull`, `@Valid`, `@Min`

### Exception Handling
`GlobalExceptionHandler.java` in `config/` handles:
- `MethodArgumentNotValidException` → 400 with field errors
- `UsernameNotFoundException` → 401
- `BadCredentialsException` → 401
- `DuplicateUsernameException` → 409
- `AccessDeniedException` → 403
- Generic `Exception` → 500

---

## Testing

### Frameworks
* JUnit 5, Mockito, Spring Boot Test, Testcontainers (for integration tests)

### Test Location
`src/test/java/com/phillipe/NutriFit/` — mirrors production package structure.

### What to Test
**Services (highest priority):** Unit test with Mockito — mock repositories, verify behavior, test error paths.

**Controllers:** Use `@WebMvcTest` with `@MockitoBean` for the service layer.

**Important:** Run `./mvnw test` WITHOUT sourcing `NutriFit-backend.env` — the env file sets `SPRING_PROFILES_ACTIVE=local` which overrides Testcontainers datasource config.

### Test Coverage
> ✅ Controller tests: `WorkoutLogControllerTest`, `WorkoutPlanControllerTest`, `UserControllerTest`, `ExerciseControllerTest`, `HealthControllerTest`, `ProfileControllerTest`, `MeasurementControllerTest`
>
> ✅ Service tests: `ProfileServiceImplTest`, `MeasurementServiceImplTest`, `WorkoutPlanServiceImplTest`

### Mockito Pattern
```java
@ExtendWith(MockitoExtension.class)
class WorkoutLogServiceImplTest {

    @Mock private WorkoutLogRepository workoutLogRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private WorkoutLogServiceImpl service;

    @Test
    void createWorkout_shouldPersistForUser() {
        // arrange + act + assert
    }
}
```

---

## Critical Patterns

### JWT Secret
```java
// ✅ CORRECT
@Value("${jwt.secret}")
private String jwtSecret;

@PostConstruct
private void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
}
```
Never generate the secret in a constructor. Must be ≥32 chars and match nutrition-service.

### Database Changes
- NEVER use `ddl-auto=update` in production
- All schema changes via Flyway migrations in `src/main/resources/db/migration/`
- Fix the duplicate V5 issue before adding any V6 migration
