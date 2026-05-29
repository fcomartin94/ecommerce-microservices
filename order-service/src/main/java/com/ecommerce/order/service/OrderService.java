package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderReserveRequest;
import com.ecommerce.order.dto.OrderReserveResponse;
import com.ecommerce.order.dto.ProductStockResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;

/**
 * Business logic for the order reservation flow.
 *
 * <p>This service implements a synchronous cross-service call: before
 * confirming a reservation, it fetches current stock from
 * {@code product-service} via a load-balanced {@link RestTemplate}
 * ({@code lb://product-service}). The stock is checked at request time;
 * no transactional stock deduction is performed (this is a demo system).</p>
 *
 * <h3>Response contract</h3>
 * <ul>
 *   <li>{@code 200} — enough stock; response includes {@code remainingStock}.</li>
 *   <li>{@code 409} — requested quantity exceeds available stock.</li>
 *   <li>{@code 404} — product-service returned {@code null} (product not found).</li>
 * </ul>
 */
@Service
public class OrderService {

    private final RestTemplate restTemplate;

    /**
     * @param restTemplate a {@code @LoadBalanced} RestTemplate that resolves
     *                     {@code lb://product-service} via Eureka
     */
    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Attempts to reserve the requested quantity of a product.
     *
     * <p>Calls {@code GET /api/products/stock/{productId}} on {@code product-service}
     * and compares the available stock against the requested quantity.</p>
     *
     * @param request contains {@code productId} and {@code quantity}
     * @return an {@link OrderReserveResponse} with {@code productId},
     *         {@code requestedQuantity}, {@code availableStock}, and {@code remainingStock}
     * @throws ResponseStatusException {@code 404} if the product does not exist
     * @throws ResponseStatusException {@code 409} if stock is insufficient
     */
    public OrderReserveResponse reserve(OrderReserveRequest request) {
        ProductStockResponse stock = restTemplate.getForObject(
                "http://product-service/api/products/stock/{productId}",
                ProductStockResponse.class,
                request.productId()
        );

        if (stock == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + request.productId());
        }

        if (stock.stock() < request.quantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient stock for productId=" + request.productId()
            );
        }

        int remaining = stock.stock() - request.quantity();
        return new OrderReserveResponse(
                request.productId(),
                request.quantity(),
                stock.stock(),
                remaining
        );
    }
}
