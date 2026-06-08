package com.ecommerce.order.dto;

/**
 * Internal DTO for deserializing the stock response from {@code product-service}.
 *
 * <p>Mapped from {@code GET /api/products/stock/{productId}} on
 * {@code product-service}. This record is only used inside
 * {@link com.ecommerce.order.service.OrderService} and is never exposed
 * to external clients.</p>
 *
 * @param productId the product whose stock was queried
 * @param stock     the current available quantity
 */
public record ProductStockResponse(
        Long productId,
        int stock
) {}

