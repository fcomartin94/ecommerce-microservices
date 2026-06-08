package com.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the API Gateway service.
 *
 * <p>Single public entry point for the platform (port {@code 8080}).
 * Uses Spring Cloud Gateway to route requests to downstream microservices
 * via Eureka service discovery ({@code lb://} URIs):</p>
 * <ul>
 *   <li>{@code /api/users/**}    → {@code lb://user-service}</li>
 *   <li>{@code /api/products/**} → {@code lb://product-service}</li>
 *   <li>{@code /api/orders/**}   → {@code lb://order-service}</li>
 * </ul>
 * <p>Routes are configured in {@code application.yml}.
 * Authentication (JWT) is handled by {@code user-service}, not here.</p>
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}