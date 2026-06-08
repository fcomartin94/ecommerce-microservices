package com.ecommerce.product.service;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Business logic for product catalogue and stock management.
 *
 * <p>Products are persisted in PostgreSQL via {@link ProductRepository}.
 * The database schema is created on first startup by Hibernate
 * ({@code ddl-auto: create-drop}), and {@link com.ecommerce.product.ProductDataInitializer}
 * seeds two sample products (Keyboard, Mouse) when the table is empty.</p>
 *
 * <p>The {@code getStock} method is consumed by {@code order-service} through
 * the {@code GET /api/products/stock/{productId}} endpoint to validate
 * reservation requests.</p>
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * @param productRepository JPA repository backed by the PostgreSQL {@code products} table
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Returns all products in the catalogue.
     *
     * @return a list of all {@link Product} entities; empty list if none exist
     */
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a single product by its ID.
     *
     * @param id the product primary key
     * @return the matching {@link Product}
     * @throws RuntimeException if no product with the given ID exists
     */
    public Product getById(Long id) {
        // findById devuelve Optional<Product>; si está vacío, lanzar 404 en lugar de RuntimeException.
        // ResponseStatusException es capturada por Spring MVC y serializada automáticamente como JSON de error.
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + id));
    }

    /**
     * Returns the current stock quantity for the given product.
     *
     * <p>Called by {@code order-service} via the stock endpoint before
     * confirming a reservation.</p>
     *
     * @param productId the product primary key
     * @return the current stock as an integer
     * @throws RuntimeException if the product does not exist
     */
    public int getStock(Long productId) {
        // Reutilizar getById para no duplicar el manejo de 404.
        // Si el producto no existe, getById ya lanza ResponseStatusException(404).
        return getById(productId).getStock();
    }
}
