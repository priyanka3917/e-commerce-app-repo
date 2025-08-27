package com.orderService.service;

import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.enums.OrderStatus;
import com.orderService.enums.TrackingStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface TrackingService {
    TrackingResponseDTO startTracking(@NotNull UUID order, String location);
    TrackingResponseDTO updateTracking(UUID orderId, TrackingStatus status, String location);
}
