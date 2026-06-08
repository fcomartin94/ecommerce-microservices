package com.ecommerce.order.dto;

/**
 * Response payload for a successful order reservation.
 *
 * <p>Returned by {@code POST /api/orders/reserve} with HTTP {@code 200}
 * when enough stock is available. The client can use {@code remainingStock}
 * to display updated availability without a separate catalogue query.</p>
 *
 * @param productId         the ID of the reserved product
 * @param requestedQuantity the number of units requested by the client
 * @param availableStock    the stock level at the time of the request
 * @param remainingStock    stock remaining after the reservation ({@code availableStock - requestedQuantity})
 */
public record OrderReserveResponse(
        Long productId,
        int requestedQuantity,
        int availableStock,
        int remainingStock
) {}

