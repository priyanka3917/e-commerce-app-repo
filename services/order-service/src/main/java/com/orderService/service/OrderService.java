package com.orderService.service;

import com.orderService.dto.request.OrderCreateRequestDTO;
import com.orderService.dto.response.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponseDTO createOrder(OrderCreateRequestDTO request);
    Page<OrderResponseDTO> getOrdersDetailByUserId(UUID id, Pageable pageable);
}
