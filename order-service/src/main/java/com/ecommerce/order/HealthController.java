package com.ecommerce.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Liveness probe endpoint for the Order Service.
 *
 * <p>Used by infrastructure tooling (load balancers, Kubernetes, etc.)
 * to verify that this service instance is up and reachable.</p>
 */
@RestController
@RequestMapping("/api/orders")
public class HealthController {

    /**
     * Returns a plain-text confirmation that the Order Service is running.
     *
     * <p>{@code GET /api/orders/health}</p>
     *
     * @return {@code "order-service OK"}
     */
    @GetMapping("/health")
    public String health() {
        return "order-service OK";
    }
}
