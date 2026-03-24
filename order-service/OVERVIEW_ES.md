# Order Service - Overview recruiter (ES)

`order-service` representa la orquestacion de negocio: chequea stock y devuelve resultado de reserva.

## Por que importa

- Demuestra comunicacion entre microservicios
- Codifica una regla de negocio concreta (reservar solo si hay stock)
- Devuelve resultados explicitos para exito (`200`) y conflicto (`409`)
