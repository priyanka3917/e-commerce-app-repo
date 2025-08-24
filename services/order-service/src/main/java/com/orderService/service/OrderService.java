package com.orderService.service;

import com.orderService.dto.request.OrderCreateRequestDTO;
import com.orderService.dto.response.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO createOrder(OrderCreateRequestDTO request);
}
