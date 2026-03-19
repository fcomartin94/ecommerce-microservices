package com.ecommerce.order.dto;

public record OrderReserveRequest(
        Long productId,
        int quantity
) {}

