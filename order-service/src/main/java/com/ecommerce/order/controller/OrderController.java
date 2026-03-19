package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderReserveRequest;
import com.ecommerce.order.dto.OrderReserveResponse;
import com.ecommerce.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<OrderReserveResponse> reserve(@RequestBody OrderReserveRequest request) {
        return ResponseEntity.ok(orderService.reserve(request));
    }
}

