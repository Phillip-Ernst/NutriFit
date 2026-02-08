# Claude Instructions for NutriFit Backend (Spring Boot API)

## Project Overview

NutriFit is a Java Spring Boot backend API for a nutrition + fitness tracking app.
It manages users, authentication, meal logging (with embedded food entries), workout
logging, and workout plan templates.

The backend is a Maven-based Spring Boot app with PostgreSQL persistence, stateless
JWT auth, and a traditional layered package organization.

---

## Architecture Patterns

### 1. Traditional Layered Package Structure

The codebase is organized by **layer/responsibility**. All packages are under:

`src/main/java/com/phillipe/NutriFit/`

```
com/phillipe/NutriFit/
├── NutriFitApplication.java
├── config/                    # Configuration classes
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   ├── GlobalExceptionHandler.java
│   └── filter/
│       └── JwtFilter.java
├── controller/                # REST controllers
│   ├── UserController.java
│   ├── MealLogController.java
│   ├── WorkoutLogController.java
│   ├── WorkoutPlanController.java
│   ├── ExerciseController.java
│   └── HealthController.java
├── service/                   # Business logic (interfaces + implementations)
│   ├── JwtService.java
│   ├── MyUserDetailsService.java
│   ├── UserService.java
│   ├── MealLogService.java
│   ├── MealLogServiceImpl.java
│   ├── WorkoutLogService.java
│   ├── WorkoutLogServiceImpl.java
│   ├── WorkoutPlanService.java
│   └── WorkoutPlanServiceImpl.java
├── repository/                # Spring Data repositories
│   ├── UserRepository.java
│   ├── MealLogRepository.java
│   ├── WorkoutLogRepository.java
│   ├── WorkoutPlanRepository.java
│   └── WorkoutPlanDayRepository.java
├── model/                     # Domain models
│   ├── ExerciseCategory.java        # Enum
│   ├── PredefinedExercise.java      # Enum
│   ├── entity/                      # JPA entities
│   │   ├── User.java
│   │   ├── MealLog.java
│   │   ├── WorkoutLog.java
│   │   ├── WorkoutPlan.java
│   │   └── WorkoutPlanDay.java
│   └── embedded/                    # Embeddable types
│       ├── MealFoodEntry.java
│       ├── WorkoutExerciseEntry.java
│       └── WorkoutPlanExercise.java
├── dto/                       # API contracts
│   ├── request/
│   │   ├── FoodItemRequest.java
│   │   ├── MealLogRequest.java
│   │   ├── ExerciseItemRequest.java
│   │   ├── WorkoutLogRequest.java
│   │   ├── WorkoutLogFromPlanRequest.java
│   │   ├── WorkoutPlanRequest.java
│   │   ├── WorkoutPlanDayRequest.java
│   │   └── WorkoutPlanExerciseRequest.java
│   └── response/
│       ├── ErrorResponse.java
│       ├── FoodItemResponse.java
│       ├── LoginResponse.java
│       ├── MealLogResponse.java
│       ├── PredefinedExerciseResponse.java
│       ├── UserResponse.java
│       ├── WorkoutLogResponse.java
│       ├── WorkoutPlanDayResponse.java
│       ├── WorkoutPlanExerciseResponse.java
│       └── WorkoutPlanResponse.java
└── security/                  # Security-related classes
    └── UserPrincipal.java
```

### 2. Layered Responsibility

Responsibilities are separated by layer:

- **Model layer (`model/`)**: persistence entities (`entity/`), embedded types (`embedded/`), enums
- **DTO layer (`dto/`)**: request/response shapes (API boundary)
- **Service layer (`service/`)**: business logic (main unit of behavior)
- **Controller layer (`controller/`)**: HTTP routing + auth principal extraction
- **Repository layer (`repository/`)**: persistence access with Spring Data
- **Config layer (`config/`)**: Spring configuration, security, filters
- **Security layer (`security/`)**: authentication-related classes

---

## Build & Run Commands

```bash
# Build
./mvnw clean package

# Run (development)
./mvnw spring-boot:run

# Run (compiled jar)
java -jar target/NutriFit-0.0.1-SNAPSHOT.jar

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=NutriFitApplicationTests

# Run a single test method
./mvnw test -Dtest=NutriFitApplicationTests#contextLoads
```

---

## Environment & Configuration

### PostgreSQL
Requires PostgreSQL. DB connection is configured via environment variables in NutriFit-backend.env (gitignored);
* `DB_HOST`
* `DB_PORT`
* `DB_NAME`
* `DB_USER`
* `DB_PASSWORD`

### Server / API Base Path
* Server port: **8080**
* Base API path: `/api`

---

### Security & Authentication
#### JWT (Stateless)
* Stateless JWT authentication
* Public endpoints: `/register` and `/login`
* All other endpoints require:
  * `Authorization: Bearer <token>`

#### Token Details
* Token expiry: **1 hour**
* Password hashing: **BCrypt strength 12**

> ✅ **JWT secret externalized** — JWT secret is loaded from `JWT_SECRET` environment variable.
> `JwtService.java` validates minimum 32-character key length for HS256 security.
> Falls back to generated secret only in dev when env var is missing (logged as warning).

#### Spring Security Configuration
* Security config lives in: `config/SecurityConfig.java`
* `JwtFilter` lives in: `config/filter/JwtFilter.java`
* CSRF is disabled (API-only)
* Session policy is stateless

---

### Database & ORM
#### JPA/Hibernate
* Uses JPA/Hibernate
* `ddl-auto=validate` (Hibernate validates schema matches entities)

#### Flyway Migrations
> ✅ **Implemented** — Flyway manages database schema migrations.

* Migration files: `src/main/resources/db/migration/V{version}__{description}.sql`
* Current migrations:
  - `V1__initial_schema.sql` — Creates all tables (users, meal_log, workout_log, workout_plan, etc.)
* `baseline-on-migrate: true` — Allows Flyway to work with existing databases
* Tests use `ddl-auto=create-drop` with Flyway disabled for isolation

#### Relationships
* User (1) → many MealLog
* User (1) → many WorkoutLog
* User (1) → many WorkoutPlan
* WorkoutPlan (1) → many WorkoutPlanDay
* WorkoutLog → optional WorkoutPlanDay
* MealLog (1) → many MealFoodEntry via @ElementCollection
* WorkoutLog (1) → many WorkoutExerciseEntry via @ElementCollection
* WorkoutPlanDay (1) → many WorkoutPlanExercise via @ElementCollection

#### Persistence Rules & Conventions
* Prefer DTOs at the API boundary
  * Don't return entities directly from controllers unless explicitly intended.

* Use LAZY by default for relationships like @ManyToOne(fetch = LAZY)
* Avoid JSON recursion
  * Prefer mapping to DTOs; only use Jackson annotations if unavoidable.
* Use UTC timestamps
  * Prefer Instant for createdAt/updatedAt style fields.

---

### API Endpoints

All endpoints are under `/api`:

**Documentation:**
* Swagger UI: `/swagger-ui.html`
* OpenAPI spec: `/api-docs`

**User/Auth:**
* `POST /register` — public, creates user, returns `UserResponse`
* `POST /login` — public, returns `{"token": "..."}`

**Meals:**
* `POST /meals` — authenticated, creates meal log with food items
* `GET /meals/mine` — authenticated, returns current user's meals (newest first)

**Workouts:**
* `POST /workouts` — authenticated, creates workout log
* `POST /workouts/from-plan` — authenticated, creates workout from plan day
* `GET /workouts/mine` — authenticated, returns current user's workouts

**Workout Plans:**
* `POST /workout-plans` — authenticated, creates workout plan
* `GET /workout-plans/mine` — authenticated, returns current user's plans
* `GET /workout-plans/{id}` — authenticated, returns specific plan
* `PUT /workout-plans/{id}` — authenticated, updates plan
* `DELETE /workout-plans/{id}` — authenticated, deletes plan
* `GET /workout-plans/days/{dayId}` — authenticated, returns specific day

**Exercises:**
* `GET /exercises/predefined` — returns predefined exercises (optional category filter)
* `GET /exercises/categories` — returns all exercise categories

---

### Code Patterns & Conventions
#### Lombok
* Lombok annotations are used on models and DTOs:
  * `@Data`, `@Builder`, `@Getter`, `@Setter`
* Note: avoid Lombok patterns that interfere with JPA proxying or required constructors.

#### DTO Organization
* Request DTOs: `dto/request/`
* Response DTOs: `dto/response/`

#### Repository Pattern
* Repositories live in `repository/` package
* Repositories extend `JpaRepository`
* Named with `*Repository` suffix (e.g., `UserRepository`)

#### Validation
Use Jakarta validation on request DTOs and controller inputs:
* `@NotEmpty`, `@NotBlank`, `@Valid`

---

### Testing
#### Testing Frameworks
* JUnit 5 (Jupiter)
* Mockito for mocking
* Spring Boot Test only when needed (prefer pure unit tests)

#### Mandatory Rule: Add/Update Unit Tests with Every Change
When creating or modifying any backend file, Claude must also create or update
corresponding unit tests (using Mockito) unless the change is purely
formatting/comments.

#### Test Location & Naming
##### Place tests under:
`src/test/java/com/phillipe/NutriFit/`
Mirror the production package structure and naming:
* Class under .../service/... → test under .../service/...
* Test naming: {ClassName}Test

Examples:
* MealLogServiceImpl → MealLogServiceImplTest
* UserService → UserServiceTest
### What to Test (by layer)
Services (Highest Priority)
* Unit test all service implementations using Mockito
* Mock repositories and external dependencies
* Verify:
  * returned values
  * error/exception paths
  * interactions (`verify(...)`, `verifyNoMoreInteractions(...)`)
  * edge cases (null/empty inputs, auth user missing, etc.)

#### Controllers (Unit-Style)
Prefer unit-style controller tests without starting the server:
* Instantiate controller with mocked service(s)
* If testing Spring MVC mappings is necessary, use @WebMvcTest
sparingly (still mock the service layer).

#### Repositories
* Do not write Mockito unit tests for pure JpaRepository interfaces.
* If a repository has custom query logic that must be validated, prefer
an integration test with Testcontainers (optional; only if requested).

#### Models / DTOs
* Do NOT write unit tests for simple Lombok getters/setters/builders.
* Only test models/DTOs if they contain non-trivial logic.

#### Mockito Patterns (Preferred)
Use JUnit 5 + Mockito extension:
```
@ExtendWith(MockitoExtension.class)
class MealLogServiceImplTest {

  @Mock
  private MealLogRepository mealLogRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private MealLogServiceImpl service;

  @Test
  void createMeal_shouldPersistMealForUser() {
    // arrange
    // when(...).thenReturn(...)

    // act
    // service.createMeal(...)

    // assert
    // verify(...)
  }
}

```
Rules:
* Use @ExtendWith(MockitoExtension.class) (not MockitoAnnotations.openMocks)
* Prefer when(...).thenReturn(...) and verify(...)
* Use ArgumentCaptor when verifying complex objects
* Assert using JUnit assertions (assertEquals, assertThrows, etc.)

Coverage Expectations
* Every service implementation should have tests covering:
  * happy path
  * at least one failure path
  * at least one edge case
* Keep tests fast and deterministic (no DB/network).

> ✅ **Controller test coverage:** MockMvc tests exist for all controllers:
> - `MealLogControllerTest`, `WorkoutLogControllerTest`, `WorkoutPlanControllerTest`
> - `UserControllerTest`, `ExerciseControllerTest`, `HealthControllerTest`

How to run tests
```
./mvnw test
```

---

### Guidelines for Code Changes
#### When Adding New Features
1. Start with DTOs + service behavior
2. Update entities and relationships only as needed
3. Implement or adjust repositories (queries) in repository/
4. Add controller endpoints with proper auth rules
5. Add validation (jakarta.validation) for request DTOs
6. Provide curl/Postman examples for new/changed endpoints
7. Add/adjust tests when practical

#### "Don't surprise me" rules
* Don't rename packages/folders broadly unless asked.
* Don't introduce new frameworks/dependencies unless necessary.
* Keep diffs small and focused.

#### If uncertain

Ask whether the user wants:
* a quick patch
* or a proper refactor
Prefer existing patterns already established in the repo.

---

## Critical Patterns to Follow

### JWT Configuration
```java
// ❌ WRONG: Secret regenerated on every restart
private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

// ✅ CORRECT: Secret from environment
@Value("${jwt.secret}")
private String jwtSecret;

@PostConstruct
private void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
}
```
- NEVER generate JWT secret in constructor
- Use `@Value("${jwt.secret}")` from environment
- Minimum 256-bit (32 character) secret for HS256

### Exception Handling
> ✅ **Implemented** — `GlobalExceptionHandler.java` in `config/` package.

All controllers have validation errors handled consistently via `@RestControllerAdvice`:
- `MethodArgumentNotValidException` → field-level validation errors
- `UsernameNotFoundException` → 401 Unauthorized
- `BadCredentialsException` → 401 Unauthorized
- `AccessDeniedException` → 403 Forbidden
- Generic exceptions → 500 Internal Server Error with safe message

Error response format matches the root CLAUDE.md specification.

### CORS Configuration
> ✅ **Implemented** — `WebConfig.java` has explicit origin allowlist from `CORS_ALLOWED_ORIGINS` env var.

```java
// ❌ WRONG: Reflects any origin (security vulnerability)
response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

// ✅ CORRECT: Explicit allowlist (current implementation)
@Value("${cors.allowed-origins}")
private String allowedOrigins;  // Comma-separated list

@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOrigins(allowedOrigins.split(","))
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowCredentials(true);
}
```

**Note:** CORS is disabled in `SecurityConfig.java` (`cors.disable()`) because it's handled manually via `WebConfig`. This works but may cause confusion.

### Database Changes
- NEVER use `ddl-auto=update` in production
- All schema changes must go through Flyway migrations
- Migration files: `src/main/resources/db/migration/V{version}__{description}.sql`
- Test migrations in CI before deployment

### Null Safety in Services
```java
// ❌ WRONG: May throw NPE if user not found
User user = userRepository.findByUsername(username);
return mealLogRepository.findByUser(user); // NPE if user is null

// ✅ CORRECT: Handle missing user explicitly
User user = userRepository.findByUsername(username)
    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
return mealLogRepository.findByUser(user);
```

---

## Remaining Tech Debt

### Low Priority

#### Hardcoded ROLE_USER
**File:** `security/UserPrincipal.java`

All users get `ROLE_USER` without role-based authorization support:
```java
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
}
```
Consider adding role-based authorization if needed in the future.

#### Short Method Names
`nz()` helper in service implementations could be more descriptive (`nullToZero()`)

---
