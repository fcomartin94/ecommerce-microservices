package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderReserveRequest;
import com.ecommerce.order.dto.OrderReserveResponse;
import com.ecommerce.order.dto.ProductStockResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private final RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OrderReserveResponse reserve(OrderReserveRequest request) {
        ProductStockResponse stock = restTemplate.getForObject(
                "http://product-service/api/products/stock/{productId}",
                ProductStockResponse.class,
                request.productId()
        );

        if (stock == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + request.productId());
        }

        if (stock.stock() < request.quantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient stock for productId=" + request.productId()
            );
        }

        int remaining = stock.stock() - request.quantity();
        return new OrderReserveResponse(
                request.productId(),
                request.quantity(),
                stock.stock(),
                remaining
        );
    }
}

