#!/bin/bash
# Creates additional databases on first PostgreSQL container startup.
# This script runs automatically via docker-entrypoint-initdb.d.
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE nutrifit_nutrition;
    GRANT ALL PRIVILEGES ON DATABASE nutrifit_nutrition TO $POSTGRES_USER;
EOSQL
