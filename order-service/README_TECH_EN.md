# Order Service - Technical README (EN)

## Purpose

`order-service` validates requested quantities against product stock and returns reservation results.

## Runtime

- Service name: `order-service`
- Port: `8083`
- Integration: calls `product-service` via load-balanced service name

## Main endpoint

- `POST /api/orders/reserve`

Example payload:

```json
{ "productId": 1, "quantity": 2 }
```

## Behavior

- `200` when stock is enough (`remainingStock` included)
- `409` when stock is insufficient

## Local run

```bash
cd order-service
mvn spring-boot:run -DskipTests
```

## Related docs

- Overview: [`OVERVIEW_EN.md`](OVERVIEW_EN.md)
- Monorepo: [`../README_EN.md`](../README_EN.md)
