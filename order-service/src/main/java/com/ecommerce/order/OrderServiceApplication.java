package com.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Order Service.
 *
 * <p>Manages order reservation requests on port {@code 8083}.
 * Before confirming a reservation, this service synchronously validates
 * stock availability by calling {@code product-service} via a
 * load-balanced {@code RestTemplate} ({@code lb://product-service}).</p>
 *
 * @see com.ecommerce.order.service.OrderService
 */
@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
