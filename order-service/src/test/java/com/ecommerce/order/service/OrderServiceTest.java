package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderReserveRequest;
import com.ecommerce.order.dto.OrderReserveResponse;
import com.ecommerce.order.dto.ProductStockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(restTemplate);
    }

    @Test
    void reserve_successfulReservation_returnsRemainingStock() {
        when(restTemplate.getForObject(any(String.class), eq(ProductStockResponse.class), eq(1L)))
                .thenReturn(new ProductStockResponse(1L, 10));

        OrderReserveResponse response = orderService.reserve(new OrderReserveRequest(1L, 3));

        assertThat(response.remainingStock()).isEqualTo(7);
        assertThat(response.requestedQuantity()).isEqualTo(3);
        assertThat(response.availableStock()).isEqualTo(10);
    }

    @Test
    void reserve_insufficientStock_throws409() {
        when(restTemplate.getForObject(any(String.class), eq(ProductStockResponse.class), eq(1L)))
                .thenReturn(new ProductStockResponse(1L, 1));

        assertThatThrownBy(() -> orderService.reserve(new OrderReserveRequest(1L, 5)))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void reserve_productNotFound_throws404() {
        when(restTemplate.getForObject(any(String.class), eq(ProductStockResponse.class), eq(99L)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertThatThrownBy(() -> orderService.reserve(new OrderReserveRequest(99L, 1)))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void reserve_nullResponse_throws404() {
        when(restTemplate.getForObject(any(String.class), eq(ProductStockResponse.class), eq(99L)))
                .thenReturn(null);

        assertThatThrownBy(() -> orderService.reserve(new OrderReserveRequest(99L, 1)))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
