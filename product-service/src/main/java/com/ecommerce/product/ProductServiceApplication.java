package com.ecommerce.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Product Service.
 *
 * <p>Manages the product catalogue and stock on port {@code 8082},
 * backed by a PostgreSQL database. Exposes endpoints for listing products,
 * retrieving individual items, and querying stock levels. The stock endpoint
 * is consumed internally by {@code order-service} during reservation.</p>
 *
 * <p>Sample data (Keyboard, Mouse) is seeded on first startup via
 * {@link com.ecommerce.product.ProductDataInitializer}.</p>
 *
 * @see com.ecommerce.product.service.ProductService
 */
@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
