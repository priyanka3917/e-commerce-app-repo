package com.orderService.dto.response;

import com.orderService.enums.TrackingStatus;
import java.time.Instant;
import java.util.UUID;

public record TrackingHistoryDTO(
        UUID id,
        TrackingStatus status,
        String location,
        Instant updatedAt
) {}
