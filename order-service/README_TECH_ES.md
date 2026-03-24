# Order Service - README tecnico (ES)

## Proposito

`order-service` valida cantidades solicitadas contra stock de productos y responde el resultado de reserva.

## Runtime

- Nombre de servicio: `order-service`
- Puerto: `8083`
- Integracion: llamada a `product-service` por nombre de servicio balanceado

## Endpoint principal

- `POST /api/orders/reserve`

Ejemplo de payload:

```json
{ "productId": 1, "quantity": 2 }
```

## Comportamiento

- `200` cuando hay stock suficiente (incluye `remainingStock`)
- `409` cuando el stock es insuficiente

## Ejecucion local

```bash
cd order-service
mvn spring-boot:run -DskipTests
```

## Documentacion relacionada

- Version tecnica EN: `README_TECH_EN.md`
- Overview recruiter EN: `OVERVIEW_EN.md`
- Overview recruiter ES: `OVERVIEW_ES.md`
