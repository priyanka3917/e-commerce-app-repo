package com.orderService.dto.response;

import com.orderService.enums.OrderStatus;
import com.orderService.enums.TrackingStatus;

import java.time.Instant;
import java.util.UUID;

public record TrackingResponseDTO(
        UUID id,
        TrackingStatus currentStatus,
        String location,
        Instant lastUpdated
) {
}
