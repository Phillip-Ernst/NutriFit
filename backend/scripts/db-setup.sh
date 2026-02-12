#!/bin/bash
# Database setup script for local development
# Usage: ./scripts/db-setup.sh [command]
#
# Commands:
#   start     - Start PostgreSQL container
#   stop      - Stop PostgreSQL container
#   reset     - Reset database (delete all data and recreate)
#   migrate   - Run Flyway migrations
#   info      - Show migration status
#   shell     - Open psql shell

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Default database settings (matching docker-compose.yml)
export DB_HOST="${DB_HOST:-localhost}"
export DB_PORT="${DB_PORT:-5432}"
export DB_NAME="${DB_NAME:-postgres}"
export DB_USER="${DB_USER:-phillipernst}"
export DB_PASSWORD="${DB_PASSWORD:-NutriFit_post_5432}"
export DB_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"

cd "$PROJECT_DIR"

case "${1:-help}" in
    start)
        echo "Starting PostgreSQL..."
        docker-compose up -d
        echo "Waiting for database to be ready..."
        sleep 3
        echo "PostgreSQL is running on port 5432"
        echo "  Database: $DB_NAME"
        echo "  User: $DB_USER"
        echo "  Password: $DB_PASSWORD"
        ;;

    stop)
        echo "Stopping PostgreSQL..."
        docker-compose down
        ;;

    reset)
        echo "Resetting database..."
        docker-compose down -v
        docker-compose up -d
        echo "Waiting for database to be ready..."
        sleep 3
        echo "Database reset complete. Run './scripts/db-setup.sh migrate' to apply migrations."
        ;;

    migrate)
        echo "Running Flyway migrations..."
        ./mvnw flyway:migrate -Dflyway.url="$DB_URL" -Dflyway.user="$DB_USER" -Dflyway.password="$DB_PASSWORD" -Dflyway.baselineOnMigrate=true -Dflyway.baselineVersion=2
        ;;

    info)
        echo "Flyway migration status:"
        ./mvnw flyway:info -Dflyway.url="$DB_URL" -Dflyway.user="$DB_USER" -Dflyway.password="$DB_PASSWORD"
        ;;

    shell)
        echo "Connecting to database..."
        docker exec -it nutrifit-db psql -U "$DB_USER" -d "$DB_NAME"
        ;;

    help|*)
        echo "NutriFit Database Management"
        echo ""
        echo "Usage: ./scripts/db-setup.sh [command]"
        echo ""
        echo "Commands:"
        echo "  start     Start PostgreSQL container"
        echo "  stop      Stop PostgreSQL container"
        echo "  reset     Reset database (delete all data and recreate)"
        echo "  migrate   Run Flyway migrations"
        echo "  info      Show migration status"
        echo "  shell     Open psql shell"
        echo "  help      Show this help message"
        echo ""
        echo "Quick Start:"
        echo "  ./scripts/db-setup.sh start"
        echo "  ./scripts/db-setup.sh migrate"
        echo "  ./mvnw spring-boot:run -Dspring-boot.run.profiles=local"
        ;;
esac
