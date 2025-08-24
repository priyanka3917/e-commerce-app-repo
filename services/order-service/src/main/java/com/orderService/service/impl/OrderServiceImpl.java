package com.orderService.service.impl;

import com.orderService.dto.request.OrderCreateRequestDTO;
import com.orderService.dto.response.OrderResponseDTO;
import com.orderService.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Override
    public OrderResponseDTO createOrder(OrderCreateRequestDTO request) {
        return null;
    }
}
