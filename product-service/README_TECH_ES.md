# Product Service - README tecnico (ES)

## Proposito

`product-service` expone catalogo de productos y datos de stock.

## Runtime

- Nombre de servicio: `product-service`
- Puerto: `8082`
- Base de datos: PostgreSQL
- Inicializacion: inserta productos de ejemplo si el repositorio esta vacio

## Endpoints principales

- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/products/stock/{productId}`

## Variables de entorno

- `DB_HOST` (default `localhost`)
- `DB_PORT` (default `5432`)
- `DB_NAME` (default `ecommerce`)
- `DB_USER` (default `postgres`)
- `DB_PASSWORD` (default `postgres`)

## Ejecucion local

```bash
cd product-service
mvn spring-boot:run -DskipTests
```

## Documentacion relacionada

- Version tecnica EN: `README_TECH_EN.md`
- Overview recruiter EN: `OVERVIEW_EN.md`
- Overview recruiter ES: `OVERVIEW_ES.md`
