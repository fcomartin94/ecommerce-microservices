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

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> list() {
        return productService.listAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id:[0-9]+}")
    public ProductDto getById(@PathVariable("id") Long id) {
        return toDto(productService.getById(id));
    }

    // Endpoint usado por order-service para validar stock
    @GetMapping("/stock/{productId}")
    public StockResponse stock(@PathVariable("productId") Long productId) {
        return new StockResponse(productId, productService.getStock(productId));
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock() == null ? 0 : product.getStock()
        );
    }
}

