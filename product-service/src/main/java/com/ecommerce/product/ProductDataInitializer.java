package com.ecommerce.product;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.save(new Product(
                "Keyboard",
                "Mechanical keyboard",
                new BigDecimal("79.99"),
                25
        ));

        productRepository.save(new Product(
                "Mouse",
                "Wireless mouse",
                new BigDecimal("29.50"),
                40
        ));
    }
}

