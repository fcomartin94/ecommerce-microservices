# API Gateway - Overview recruiter (ES)

`api-gateway` facilita el consumo del proyecto: un unico endpoint publico (`:8080`) que redirige llamadas a todos los microservicios.

## Por que importa

- Demuestra patron API Gateway en arquitectura de microservicios
- Usa enrutado balanceado con Eureka (`lb://...`)
- Desacopla al cliente de la topologia interna de servicios

## Mensaje de valor rapido

Este modulo es la puerta de entrada de la plataforma y centraliza rutas para usuarios, productos y pedidos.
