# NutriFit

A nutrition and fitness tracking application.

## Project Structure

```
├── backend/    — Spring Boot REST API (Java 21, PostgreSQL, JWT auth)
├── frontend/   — React SPA (TypeScript, Vite, Tailwind CSS)
├── .gitignore
└── README.md
```

## Backend

**Stack:** Java 21, Spring Boot 4.0.2, PostgreSQL, Maven, JWT (JJWT 0.12.6)

### Setup

1. Configure environment variables for PostgreSQL (`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`)
2. Run from the `backend/` directory:
   ```bash
   ./mvnw spring-boot:run
   ```
3. API runs on `http://localhost:8080/api`

### API Endpoints

| Method | Endpoint         | Auth     | Description              |
|--------|------------------|----------|--------------------------|
| POST   | `/api/register`  | Public   | Create a new user        |
| POST   | `/api/login`     | Public   | Returns JWT token        |
| POST   | `/api/meals`     | Required | Log a meal with foods    |
| GET    | `/api/meals/mine`| Required | Get current user's meals |

## Frontend

**Stack:** React 18, TypeScript, Vite, Tailwind CSS, React Router, TanStack Query

### Setup

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5173`.
