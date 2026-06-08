package com.ecommerce.order.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client configuration for the Order Service.
 *
 * <p>Exposes a load-balanced {@link RestTemplate} bean that resolves
 * {@code lb://} service names via Eureka. This enables
 * {@link com.ecommerce.order.service.OrderService} to call
 * {@code product-service} by name without hardcoded host/port values.</p>
 */
@Configuration
public class RestClientConfig {

    /**
     * Creates a {@link RestTemplate} decorated with Spring Cloud
     * {@code @LoadBalanced}, which intercepts requests and resolves
     * {@code lb://product-service} to an actual instance registered in Eureka.
     *
     * @param builder Spring Boot's auto-configured builder
     * @return a load-balanced {@link RestTemplate}
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

