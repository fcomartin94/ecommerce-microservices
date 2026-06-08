package com.ecommerce.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Liveness probe endpoint for the User Service.
 *
 * <p>Used by infrastructure tooling (load balancers, Kubernetes, etc.)
 * to verify that this service instance is up and reachable.</p>
 */
@RestController
@RequestMapping("/api/users")
public class HealthController {

    /**
     * Returns a plain-text confirmation that the User Service is running.
     *
     * <p>{@code GET /api/users/health}</p>
     *
     * @return {@code "user-service OK"}
     */
    @GetMapping("/health")
    public String health() {
        return "user-service OK";
    }
}