package com.ecommerce.product.dto;

import java.math.BigDecimal;

/**
 * Public-facing representation of a product in the catalogue.
 *
 * <p>Returned by {@code GET /api/products} and {@code GET /api/products/{id}}.
 * This DTO intentionally excludes internal JPA fields and normalises
 * a {@code null} stock to {@code 0}.</p>
 *
 * @param id          the product primary key
 * @param name        the product display name
 * @param description a short description
 * @param price       the unit price
 * @param stock       the current available quantity (never {@code null})
 */
public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int stock
) {}

