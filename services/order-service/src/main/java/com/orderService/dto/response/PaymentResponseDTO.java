package com.orderService.dto.response;

import com.orderService.enums.PaymentMethod;
import com.orderService.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID id,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String transactionId,
        BigDecimal amount,
        Instant paymentDate
) {
}
