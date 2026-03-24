# Product Service - Overview recruiter (ES)

`product-service` es la fuente de verdad del catalogo y del stock.

## Snapshot recruiter

- Patron: servicio de dominio con ownership de datos persistentes
- Senal de datos: catalogo e inventario sobre PostgreSQL
- Senal de integracion: cubre navegacion de cliente y validacion interna de stock

## Valor de negocio

Este modulo habilita decisiones de inventario consistentes y reduce errores de pedido al centralizar la validacion de stock.
