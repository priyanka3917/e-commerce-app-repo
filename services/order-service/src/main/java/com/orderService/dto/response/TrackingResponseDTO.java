package com.orderService.dto.response;

import com.orderService.enums.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record TrackingResponseDTO(
        UUID id,
        OrderStatus currentStatus,
        String trackingNumber,
        String location,
        Instant lastUpdated
) {
}
