# User Service - Overview recruiter (ES)

`user-service` gestiona registro/login y emite tokens JWT para acceso autenticado.

## Snapshot recruiter

- Patron: autenticacion stateless en backend de microservicios
- Senal de seguridad: hashing de password mas autenticacion por token
- Senal de producto: frontera de autenticacion reutilizable para clientes

## Valor de negocio

Este modulo aporta una base de autenticacion estilo produccion que protege APIs sin complicar la integracion.
