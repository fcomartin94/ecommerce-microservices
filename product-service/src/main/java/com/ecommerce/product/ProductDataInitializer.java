package com.ecommerce.product;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds the product catalogue with sample data on first startup.
 *
 * <p>Runs automatically after the application context is ready via
 * {@link CommandLineRunner}. If the {@code products} table already contains
 * rows (e.g. a restart against a persistent database), seeding is skipped.</p>
 *
 * <p>Default seed data: <em>Keyboard</em> (25 units @ $79.99) and
 * <em>Mouse</em> (40 units @ $29.50).</p>
 */
@Component
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Inserts sample products if the table is empty.
     *
     * @param args command-line arguments (unused)
     */
    @Override
    public void run(String... args) {
        // Si ya hay productos (por ejemplo, tras un reinicio sin borrar el volumen Docker),
        // no sembrar de nuevo para no duplicar datos
        if (productRepository.count() > 0) {
            return;
        }

        // Producto 1: Teclado mecánico — precio con BigDecimal para exactitud decimal
        productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("79.99"),
                25   // unidades iniciales en stock
        ));

        // Producto 2: Ratón inalámbrico
        productRepository.save(new Product(
                "Mouse",
                "Wireless mouse",
                new BigDecimal("29.50"),
                40   // unidades iniciales en stock
        ));
    }
}

