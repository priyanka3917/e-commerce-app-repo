package com.orderService.service;

import com.orderService.dto.response.PaymentResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDTO makePayment(OrderEntity order, BigDecimal amount, PaymentMethod method);
    public PaymentResponseDTO getPaymentByOrderId(UUID orderId);
}
