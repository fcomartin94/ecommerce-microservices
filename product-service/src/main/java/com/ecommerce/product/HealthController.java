package com.ecommerce.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Liveness probe endpoint for the Product Service.
 *
 * <p>Used by infrastructure tooling (load balancers, Kubernetes, etc.)
 * to verify that this service instance is up and reachable.</p>
 */
@RestController
@RequestMapping("/api/products")
public class HealthController {

    /**
     * Returns a plain-text confirmation that the Product Service is running.
     *
     * <p>{@code GET /api/products/health}</p>
     *
     * @return {@code "product-service OK"}
     */
    @GetMapping("/health")
    public String health() {
        return "product-service OK";
    }
}
