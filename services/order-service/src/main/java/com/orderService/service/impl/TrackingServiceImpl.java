package com.orderService.service.impl;

import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.entity.OrderTrackingEntity;
import com.orderService.enums.OrderStatus;
import com.orderService.mapper.OrderMapper;
import com.orderService.repository.OrderRepo;
import com.orderService.repository.TrackingRepo;
import com.orderService.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final TrackingRepo trackingRepo;
    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;

    @Transactional
    public TrackingResponseDTO startTracking(UUID orderId, String location) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        OrderTrackingEntity tracking = OrderTrackingEntity.builder()
                .order(order)
                .currentStatus(OrderStatus.PENDING) // default status
                .location(location != null ? location : "Warehouse")
                .build();

        return orderMapper.toTrackingResponseDTO(trackingRepo.save(tracking));
    }

    @Transactional
    public TrackingResponseDTO updateTracking(UUID orderId, OrderStatus status, String location) {
        OrderTrackingEntity tracking = trackingRepo.findAll().stream()
                .filter(t -> t.getOrder().getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tracking not found for order id: " + orderId));

        if (status != null) tracking.setCurrentStatus(status);
        if (location != null) tracking.setLocation(location);

        return orderMapper.toTrackingResponseDTO(trackingRepo.save(tracking));
    }
}
