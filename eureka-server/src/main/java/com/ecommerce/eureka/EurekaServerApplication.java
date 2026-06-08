package com.ecommerce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Entry point for the Eureka Service Registry.
 *
 * <p>Runs Netflix Eureka Server on port {@code 8761}. All microservices
 * ({@code user-service}, {@code product-service}, {@code order-service},
 * {@code api-gateway}) register here on startup, enabling load-balanced
 * routing via {@code lb://} URIs without hardcoded hosts.</p>
 *
 * <p>The Eureka dashboard is available at {@code http://localhost:8761}.</p>
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}