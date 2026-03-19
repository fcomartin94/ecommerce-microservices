package com.ecommerce.product.dto;

public record StockResponse(
        Long productId,
        int stock
) {}

