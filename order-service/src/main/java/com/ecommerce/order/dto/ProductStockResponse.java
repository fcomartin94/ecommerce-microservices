package com.ecommerce.order.dto;

public record ProductStockResponse(
        Long productId,
        int stock
) {}

