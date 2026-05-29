# Product Service - Technical README (EN)

## Purpose

`product-service` exposes product catalog and stock data.

## Runtime

- Service name: `product-service`
- Port: `8082`
- Database: PostgreSQL
- Seed behavior: inserts sample products if repository is empty

## Main endpoints

- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/stock/{productId}`

## Environment variables

- `DB_HOST` (default `localhost`)
- `DB_PORT` (default `5432`)
- `DB_NAME` (default `ecommerce`)
- `DB_USER` (default `postgres`)
- `DB_PASSWORD` (default `postgres`)

## Local run

```bash
cd product-service
mvn spring-boot:run -DskipTests
```

## Related docs

- Overview: [`OVERVIEW_EN.md`](OVERVIEW_EN.md)
- Monorepo: [`../README_EN.md`](../README_EN.md)
