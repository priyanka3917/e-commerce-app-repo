package com.orderService.dto.response;

import com.orderService.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID id,
        UUID userId,
        BigDecimal totalAmount,
        OrderStatus orderStatus,
        Instant orderDate,
        List<OrderItemResponseDTO> items,
        TrackingResponseDTO tracking,
        PaymentResponseDTO payment
) {
}
