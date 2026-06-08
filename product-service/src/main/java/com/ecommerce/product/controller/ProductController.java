package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.dto.StockResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the product catalogue and stock endpoints.
 *
 * <p>Base path: {@code /api/products}. All requests are routed here
 * through the API Gateway ({@code lb://product-service}).</p>
 *
 * <p>The stock endpoint ({@code /stock/{productId}}) is consumed
 * internally by {@code order-service} to validate reservation requests.</p>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns all products in the catalogue.
     *
     * <p>{@code GET /api/products}</p>
     *
     * @return list of {@link ProductDto}; empty list if no products exist
     */
    @GetMapping
    public List<ProductDto> list() {
        return productService.listAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Returns a single product by its ID.
     *
     * <p>{@code GET /api/products/{id}}</p>
     *
     * @param id the product primary key (numeric)
     * @return the matching {@link ProductDto}
     * @throws org.springframework.web.server.ResponseStatusException {@code 404} if not found
     */
    @GetMapping("/{id:[0-9]+}")
    public ProductDto getById(@PathVariable("id") Long id) {
        return toDto(productService.getById(id));
    }

    /**
     * Returns the current stock for a given product.
     *
     * <p>{@code GET /api/products/stock/{productId}}</p>
     *
     * <p>This endpoint is intended for internal service-to-service use:
     * {@code order-service} calls it before confirming a reservation.</p>
     *
     * @param productId the product primary key
     * @return a {@link StockResponse} with the product ID and available stock
     * @throws org.springframework.web.server.ResponseStatusException {@code 404} if not found
     */
    @GetMapping("/stock/{productId}")
    public StockResponse stock(@PathVariable("productId") Long productId) {
        return new StockResponse(productId, productService.getStock(productId));
    }

    /**
     * Maps a {@link Product} entity to its public-facing {@link ProductDto}.
     * Treats a {@code null} stock value as {@code 0}.
     */
    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                // Normalizar null a 0: la columna tiene NOT NULL pero puede ser null en memoria durante tests
                product.getStock() == null ? 0 : product.getStock()
        );
    }
}

