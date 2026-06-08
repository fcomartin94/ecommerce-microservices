package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for {@link com.ecommerce.product.model.Product} entities.
 *
 * <p>Backed by the PostgreSQL {@code products} table. Inherits standard
 * CRUD and pagination operations from {@link JpaRepository}. No custom
 * queries are needed beyond what Spring Data JPA provides out of the box.</p>
 */
public interface ProductRepository extends JpaRepository<Product, Long> {}

