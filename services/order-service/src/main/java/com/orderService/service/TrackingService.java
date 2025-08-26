package com.orderService.service;

import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface TrackingService {
    TrackingResponseDTO startTracking(@NotNull UUID order, String location);
    TrackingResponseDTO updateTracking(UUID orderId, OrderStatus status, String location);
}
