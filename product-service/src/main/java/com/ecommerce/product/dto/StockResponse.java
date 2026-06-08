package com.ecommerce.product.dto;

/**
 * Response payload for the stock query endpoint.
 *
 * <p>Returned by {@code GET /api/products/stock/{productId}}.
 * Consumed internally by {@code order-service} to validate stock
 * before confirming a reservation.</p>
 *
 * @param productId the product whose stock was queried
 * @param stock     the current available quantity
 */
public record StockResponse(
        Long productId,
        int stock
) {}

