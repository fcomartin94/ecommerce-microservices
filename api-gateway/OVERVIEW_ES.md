# API Gateway - Overview recruiter (ES)

`api-gateway` es el punto de entrada de la plataforma: una unica URL publica (`:8080`) que enruta trafico a todos los servicios backend.

## Snapshot recruiter

- Patron: API Gateway aplicado en una arquitectura de microservicios real
- Resultado: integracion de clientes mas simple y limites de servicio mas claros
- Senal de robustez: enrutado con descubrimiento via Eureka (`lb://...`)

## Valor de negocio

Este modulo centraliza rutas de usuarios, productos y pedidos, reduciendo friccion de integracion y facilitando escalabilidad.
