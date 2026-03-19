package com.ecommerce.order.dto;

public record OrderReserveResponse(
        Long productId,
        int requestedQuantity,
        int availableStock,
        int remainingStock
) {}

