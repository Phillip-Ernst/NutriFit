# NutriFit

A full-stack nutrition and fitness tracking app built as a microservices architecture. Users can log meals, track workouts and plans, and monitor body measurements over time.

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  nginx / ALB                    │
│  /api/meals* → nutrition-service  (port 8081)   │
│  /api/*      → backend            (port 8080)   │
│  /           → frontend           (port 5173)   │
└─────────────────────────────────────────────────┘
```

| Service | Stack | Purpose |
|---|---|---|
| `backend/` | Java 21, Spring Boot, PostgreSQL | Auth, workouts, plans, profile, measurements |
| `nutrition-service/` | Java 21, Spring Boot, PostgreSQL | Meal logging (extracted microservice) |
| `frontend/` | React 19, TypeScript, Vite, Tailwind | Single SPA for both services |
| `nginx/` | nginx | Local routing config |

Both services share the same `JWT_SECRET`. The backend issues tokens on login; the nutrition-service validates them directly from JWT claims — no shared database.

## Quick Start (Local Development)

> See [CLAUDE.md](./CLAUDE.md) for full setup instructions, environment variables, and troubleshooting.

```bash
# 1. Start the database (creates both nutrifit and nutrifit_nutrition schemas)
cd backend && docker compose up -d

# 2. Run the backend (new terminal)
source backend/NutriFit-backend.env
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 3. Run the nutrition service (new terminal)
source backend/NutriFit-backend.env
cd nutrition-service && ./mvnw spring-boot:run

# 4. Run the frontend (new terminal)
cd frontend && npm install && npm run dev
```

Frontend: `http://localhost:5173` · Backend API: `http://localhost:8080/api` · Nutrition API: `http://localhost:8081/api`

## Tech Stack

**Backend & Nutrition-service:** Java 21 · Spring Boot 4 · PostgreSQL · Flyway · JWT (JJWT) · Testcontainers

**Frontend:** React 19 · TypeScript · Vite · Tailwind CSS v4 · TanStack Query · React Router · Vitest

**Infrastructure:** Docker · AWS ECS Fargate · RDS · ECR · GitHub Actions

## API Overview

| Method | Endpoint | Auth | Service |
|---|---|---|---|
| POST | `/api/register` | Public | backend |
| POST | `/api/login` | Public | backend |
| GET/PUT | `/api/profile` | Required | backend |
| GET/POST/DELETE | `/api/measurements` | Required | backend |
| GET/POST/DELETE | `/api/workouts` | Required | backend |
| GET/POST/PUT/DELETE | `/api/workout-plans` | Required | backend |
| POST | `/api/meals` | Required | nutrition-service |
| GET | `/api/meals/mine` | Required | nutrition-service |
| DELETE | `/api/meals/{id}` | Required | nutrition-service |
