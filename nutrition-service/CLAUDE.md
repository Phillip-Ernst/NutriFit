# Claude Instructions for NutriFit Nutrition Service

## Project Overview

The nutrition-service is a standalone Spring Boot microservice responsible exclusively for **meal logging**. It was extracted from the main backend to allow independent scaling and deployment.

- Port: **8081**
- Base API path: `/api`
- Database: `nutrifit_nutrition` (separate from the main `nutrifit` database)
- Package: `com.phillipe.nutrifit.nutrition`
- Spring Boot version: **4.0.x**

---

## Architecture

### Key Design Decisions

1. **No UserDetailsService** — Auth is JWT-only. The filter validates the token signature and extracts the username from claims directly. No database lookup for users.

2. **`username` string, not FK** — `MealLog` stores the username as a plain `VARCHAR` column. There is no foreign key to the users table (which lives in a different database). The JWT is trusted as the identity source.

3. **Shared JWT secret** — Uses the same `JWT_SECRET` as the backend. Tokens issued by the backend are accepted here without any coordination.

4. **Spring Boot 4.x specifics:**
   - `spring-boot-starter-webmvc` (not `spring-boot-starter-web`)
   - `@WebMvcTest` is in `org.springframework.boot.webmvc.test.autoconfigure`
   - `spring-boot-webmvc-test` artifact required for controller tests
   - `ObjectMapper` is NOT auto-wired in `@WebMvcTest` — instantiate manually

---

## Package Structure

```
com/phillipe/nutrifit/nutrition/
├── NutritionServiceApplication.java
├── config/
│   ├── SecurityConfig.java          JWT-only security, no UserDetailsService
│   ├── GlobalExceptionHandler.java  Same error format as backend
│   └── filter/
│       └── JwtFilter.java           Validates JWT, sets auth from claims only
├── controller/
│   └── MealLogController.java
├── service/
│   ├── MealLogService.java          Interface
│   ├── MealLogServiceImpl.java      Uses username string directly
│   └── JwtService.java              JWT validation only (no user loading)
├── repository/
│   └── MealLogRepository.java       Queries by username, not userId
├── model/
│   ├── entity/
│   │   └── MealLog.java             username VARCHAR, not FK
│   └── embedded/
│       └── MealFoodEntry.java
└── dto/
    ├── request/
    │   ├── MealLogRequest.java
    │   └── FoodItemRequest.java
    └── response/
        ├── MealLogResponse.java
        ├── FoodItemResponse.java
        └── ErrorResponse.java
```

---

## Build & Run Commands

```bash
# Run (requires env vars — share backend's env file)
source ../backend/NutriFit-backend.env
./mvnw spring-boot:run

# Run tests (do NOT source the env file first)
./mvnw test

# Build jar
./mvnw clean package -DskipTests
```

---

## Environment & Configuration

No Spring profiles needed — datasource is configured via `SPRING_DATASOURCE_*` env vars directly.

### Required Environment Variables
| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | e.g. `jdbc:postgresql://localhost:5432/nutrifit_nutrition` |
| `SPRING_DATASOURCE_USERNAME` | e.g. `nutrifit` |
| `SPRING_DATASOURCE_PASSWORD` | DB password |
| `JWT_SECRET` | Must match the backend's JWT_SECRET |
| `CORS_ALLOWED_ORIGINS` | e.g. `http://localhost:5173` |

The env vars are shared with the backend via `backend/NutriFit-backend.env`. The `SPRING_DATASOURCE_URL` in that file should point to `nutrifit_nutrition`.

### Local Database Setup
The `nutrifit_nutrition` database is created automatically when Docker Compose starts via `backend/scripts/init-multiple-dbs.sh`.

If the database is missing:
```bash
cd backend
docker compose down -v && docker compose up -d
```

---

## API Endpoints

All endpoints are under `/api` (servlet path) and require `Authorization: Bearer <token>`:

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/meals` | Create a meal log with food items |
| `GET` | `/api/meals/mine` | Get current user's meals (newest first) |
| `GET` | `/api/actuator/health` | Health check (public) |

### Request Format — POST /api/meals
```json
{
  "foods": [
    {"type": "Chicken Breast", "calories": 165, "protein": 31, "carbs": 0, "fats": 4},
    {"type": "Brown Rice", "calories": 215, "protein": 5, "carbs": 45, "fats": 2}
  ]
}
```

### Response Format
```json
{
  "id": 1,
  "createdAt": "2026-02-25T20:00:38Z",
  "totalCalories": 380,
  "totalProtein": 36,
  "totalCarbs": 45,
  "totalFats": 6,
  "foods": [...]
}
```

---

## Database Schema

Managed by Flyway (`src/main/resources/db/migration/`):

**`V1__create_meal_tables.sql`:**
```sql
CREATE TABLE meal_log (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,   -- no FK to users table
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    total_calories INTEGER NOT NULL DEFAULT 0,
    total_protein  INTEGER NOT NULL DEFAULT 0,
    total_carbs    INTEGER NOT NULL DEFAULT 0,
    total_fats     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE meal_log_foods (
    meal_log_id BIGINT NOT NULL REFERENCES meal_log(id) ON DELETE CASCADE,
    type     VARCHAR(255),
    calories INTEGER,
    protein  INTEGER,
    carbs    INTEGER,
    fats     INTEGER
);

CREATE INDEX idx_meal_log_username ON meal_log(username);
CREATE INDEX idx_meal_log_created_at ON meal_log(created_at);
```

---

## Security

### JwtFilter
Validates the JWT signature using the shared secret. On success, sets a `UsernamePasswordAuthenticationToken` from the `sub` claim — no `UserDetailsService` or database call.

### SecurityConfig
```java
http
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .csrf(AbstractHttpConfigurer::disable)
    .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/**").permitAll()
        .anyRequest().authenticated()
    )
    .httpBasic(AbstractHttpConfigurer::disable)
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

**Note:** A "Using generated security password" warning appears at startup — this is harmless. The `UserDetailsServiceAutoConfiguration` creates an in-memory user that is never used because HTTP Basic is disabled and JWT handles all auth.

---

## Testing

### Frameworks
* JUnit 5, Mockito
* `spring-boot-webmvc-test` for `@WebMvcTest` (Spring Boot 4.x artifact)
* `spring-security-test` for `.with(csrf()).with(user("testuser"))`

### Test Location
`src/test/java/com/phillipe/nutrifit/nutrition/`

### Spring Boot 4.x @WebMvcTest Pattern
```java
@WebMvcTest(MealLogController.class)
@Import(SecurityConfig.class)
class MealLogControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean MealLogService mealLogService;

    // ObjectMapper is NOT auto-wired in SB4 @WebMvcTest — create manually
    private final ObjectMapper objectMapper = new ObjectMapper();
}
```

---

## Production Deployment (AWS ECS Fargate)

- **Cluster:** `NutriFit-Cluster`
- **Service:** `nutrifit-nutrition`
- **Task definition:** `nutrifit-nutrition` (see `scripts/nutrition-task-definition.json`)
- **ECR repo:** `nutrifit-nutrition`
- **Target group:** `nutrifit-nutrition-tg` (port 8081, health check: `/api/actuator/health`)
- **ALB rule:** Priority 5, path `/api/meals*` → nutrition target group (both HTTP and HTTPS listeners)
- **Security group:** Must allow inbound port 8081 from the ALB security group

### CI/CD
Deployed automatically via `.github/workflows/ci-cd.yml` on push to `main`:
1. Run tests (`./mvnw test -B` from `nutrition-service/`)
2. Build Docker image and push to ECR
3. Register new ECS task definition revision
4. Update ECS service with rolling deployment