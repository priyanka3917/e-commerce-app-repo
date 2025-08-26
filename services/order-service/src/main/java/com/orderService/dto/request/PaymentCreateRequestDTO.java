package com.orderService.dto.request;

import com.orderService.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreateRequestDTO(
        UUID orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}
