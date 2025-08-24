package com.orderService.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDTO(
        UUID id,
        String productId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
