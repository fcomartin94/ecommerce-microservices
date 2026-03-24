# Order Service - Overview recruiter (ES)

`order-service` es la capa de orquestacion de negocio del flujo de reserva.

## Snapshot recruiter

- Patron: orquestacion entre servicios con reglas de negocio explicitas
- Senal de producto: reserva solo cuando el stock es suficiente
- Senal de API: respuestas claras para exito (`200`) y conflicto (`409`)

## Valor de negocio

Este modulo convierte la validacion de stock en una decision de pedido predecible, mejorando confiabilidad en escenarios tipo checkout.
