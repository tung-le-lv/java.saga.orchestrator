#!/bin/bash
# Provisions one database per service on the shared Postgres instance. Runs automatically on
# container startup via /docker-entrypoint-initdb.d/ (official postgres image convention).
set -euo pipefail

for db in order_db payment_db fulfillment_db orchestrator_db; do
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE $db;
EOSQL
  echo "Created database: $db"
done
