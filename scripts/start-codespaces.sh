#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

export DB_HOST="${DB_HOST:-host.docker.internal}"
export DB_PORT="${DB_PORT:-5432}"
export DB_NAME="${DB_NAME:-ecommerce}"
export DB_USER="${DB_USER:-postgres}"
export DB_PASSWORD="${DB_PASSWORD:-postgres}"
export JWT_SECRET="${JWT_SECRET:-MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDA=}"

POSTGRES_SERVICE="${POSTGRES_SERVICE:-postgres}"

mkdir -p "$ROOT_DIR/logs"

has_cmd () {
  command -v "$1" >/dev/null 2>&1
}

http_get_ok () {
  local url="$1"
  if has_cmd curl; then
    curl -sSf "$url" >/dev/null 2>&1
    return $?
  fi
  if has_cmd wget; then
    wget -qO- "$url" >/dev/null 2>&1
    return $?
  fi
  echo "Neither curl nor wget is available; cannot check readiness." >&2
  return 1
}

echo "==> Starting Postgres (docker compose)..."
docker compose up -d "$POSTGRES_SERVICE"

echo "==> Waiting for Postgres..."
for _ in $(seq 1 60); do
  if docker compose exec -T "$POSTGRES_SERVICE" pg_isready -U "$DB_USER" -d "$DB_NAME" >/dev/null 2>&1; then
    echo "Postgres is ready."
    break
  fi
  sleep 1
done

echo "==> Starting microservices (Spring Boot)..."

start_service () {
  local service_name="$1"
  local service_dir="$2"
  shift 2
  local log_file="$ROOT_DIR/logs/${service_name}.log"

  echo "---- $service_name (log: $log_file) ----"
  (
    cd "$service_dir"
    # We rely on application.yml for ports; environment vars configure DB/JWT.
    env DB_HOST="$DB_HOST" DB_PORT="$DB_PORT" DB_NAME="$DB_NAME" DB_USER="$DB_USER" DB_PASSWORD="$DB_PASSWORD" \
      JWT_SECRET="$JWT_SECRET" \
      mvn spring-boot:run -DskipTests "$@" >"$log_file" 2>&1
  ) &
}

# Eureka first (service discovery)
start_service "eureka-server" "$ROOT_DIR/eureka-server"

# Then the rest (gateway last)
start_service "user-service" "$ROOT_DIR/user-service"
start_service "product-service" "$ROOT_DIR/product-service"
start_service "order-service" "$ROOT_DIR/order-service"
start_service "api-gateway" "$ROOT_DIR/api-gateway"

echo "==> Waiting for API Gateway to be reachable..."
for _ in $(seq 1 90); do
  if http_get_ok "http://localhost:8080/api/users/health" \
    && http_get_ok "http://localhost:8080/api/products/health" \
    && http_get_ok "http://localhost:8080/api/orders/health"; then
    echo "All health checks pass via API Gateway."
    break
  fi
  sleep 2
done

echo
echo "Recruiter quick test:"
echo "  Health:   curl http://localhost:8080/api/users/health"
echo "  Products: curl http://localhost:8080/api/products"
echo "  Reserve:  curl -s -X POST http://localhost:8080/api/orders/reserve -H 'Content-Type: application/json' -d '{\"productId\":1,\"quantity\":2}'"
echo
echo "Logs are in: $ROOT_DIR/logs/"
echo "If something fails, inspect those log files."

