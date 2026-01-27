# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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

## Environment Setup

Requires PostgreSQL. Connection configured via environment variables in `NutriFit-backend.env` (gitignored): `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.

Server runs on port 8080 with base API path `/api`.

## Architecture

**Stack:** Java 21, Spring Boot 4.0.2, PostgreSQL, Maven, JWT (JJWT 0.12.6), Lombok.

**Package structure** (`src/main/java/com/phillipe/NutriFit/`): Each domain feature is a top-level package with `model/`, `controller/`, `service/`, `dao/`, `dto/`, and `config/` sub-packages.

Domain packages:
- **User** — Registration, login, JWT auth, Spring Security config. `JwtFilter` lives under `User/config/filter/`.
- **MealLog** — Meal logging with embedded `MealFoodEntry` items. Uses interface + impl pattern (`MealLogService` / `MealLogServiceImpl`).
- **WorkoutLog** — Skeleton only, not yet implemented.

**Authentication:** Stateless JWT. `/register` and `/login` are public; all other endpoints require `Authorization: Bearer <token>`. Tokens expire after 1 hour. Passwords are BCrypt-encoded (strength 12). JWT secret key is generated in-memory on startup (not persisted — tokens invalidate on restart).

**Security config** is in `User/config/SecurityConfig.java`. CSRF disabled (API-only). Sessions are stateless.

**Database:** JPA/Hibernate with `ddl-auto=update` (auto-creates/updates tables). Relationships: User (1) → many MealLog; MealLog (1) → many MealFoodEntry (`@ElementCollection`).

## API Endpoints

All under `/api`:
- `POST /register` — public, creates user
- `POST /login` — public, returns JWT token
- `POST /meals` — authenticated, creates meal log with food items
- `GET /meals/mine` — authenticated, returns current user's meals (newest first)

## Conventions

- Lombok annotations (`@Data`, `@Builder`, `@Getter`, `@Setter`) on models and DTOs.
- Request/response DTOs in `dto/request/` and `dto/response/` within each domain package.
- Repositories extend `JpaRepository` in `dao/` packages.
- Jakarta validation (`@NotEmpty`, `@NotBlank`, `@Valid`) for request validation.
