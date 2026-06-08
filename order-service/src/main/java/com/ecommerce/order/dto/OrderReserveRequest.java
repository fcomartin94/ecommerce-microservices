package com.ecommerce.order.dto;

/**
 * Request payload for the order reservation endpoint.
 *
 * <p>Sent by clients to {@code POST /api/orders/reserve}.
 * {@code order-service} uses {@code productId} to query stock from
 * {@code product-service} before confirming the reservation.</p>
 *
 * @param productId the ID of the product to reserve
 * @param quantity  the number of units requested (must be &gt; 0)
 */
public record OrderReserveRequest(
        Long productId,
        int quantity
) {}

