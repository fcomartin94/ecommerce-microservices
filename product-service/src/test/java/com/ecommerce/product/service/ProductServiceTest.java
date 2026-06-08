package com.ecommerce.product.service;

import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void listAll_returnsAllProducts() {
        Product keyboard = new Product("Keyboard", "Mechanical", new BigDecimal("99.99"), 50);
        Product mouse = new Product("Mouse", "Wireless", new BigDecimal("49.99"), 100);
        when(productRepository.findAll()).thenReturn(List.of(keyboard, mouse));

        List<Product> result = productService.listAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Keyboard");
    }

    @Test
    void getById_existingProduct_returnsProduct() {
        Product keyboard = new Product("Keyboard", "Mechanical", new BigDecimal("99.99"), 50);
        when(productRepository.findById(1L)).thenReturn(Optional.of(keyboard));

        Product result = productService.getById(1L);

        assertThat(result.getName()).isEqualTo("Keyboard");
        assertThat(result.getStock()).isEqualTo(50);
    }

    @Test
    void getById_nonExistingProduct_throws404() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStock_existingProduct_returnsStock() {
        Product mouse = new Product("Mouse", "Wireless", new BigDecimal("49.99"), 30);
        when(productRepository.findById(2L)).thenReturn(Optional.of(mouse));

        int stock = productService.getStock(2L);

        assertThat(stock).isEqualTo(30);
    }

    @Test
    void getStock_nonExistingProduct_throws404() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getStock(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
