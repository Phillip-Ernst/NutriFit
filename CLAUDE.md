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
- Ask: “Do you want this as a quick patch or a proper refactor?”
- Prefer the existing patterns already used in the repo.
