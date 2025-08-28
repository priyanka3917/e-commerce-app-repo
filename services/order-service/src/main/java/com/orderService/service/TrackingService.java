package com.orderService.service;

import com.orderService.dto.response.TrackingHistoryDTO;
import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.entity.OrderTrackingEntity;
import com.orderService.enums.OrderStatus;
import com.orderService.enums.TrackingStatus;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface TrackingService {
    TrackingResponseDTO startTracking(@NotNull UUID order, String location);
    TrackingResponseDTO updateTracking(UUID orderId, TrackingStatus status, String location);
    List<TrackingHistoryDTO> getTrackingHistory(UUID orderId);
    void saveHistory(OrderTrackingEntity tracking, TrackingStatus trackingStatus, String location);
}
