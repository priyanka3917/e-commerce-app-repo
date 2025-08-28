package com.orderService.service.impl;

import com.orderService.dto.response.TrackingHistoryDTO;
import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.entity.OrderTrackingEntity;
import com.orderService.entity.OrderTrackingHistoryEntity;
import com.orderService.enums.TrackingStatus;
import com.orderService.mapper.OrderMapper;
import com.orderService.mapper.TrackingMapper;
import com.orderService.repository.OrderRepo;
import com.orderService.repository.TrackingHistoryRepo;
import com.orderService.repository.TrackingRepo;
import com.orderService.service.TrackingService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingServiceImpl implements TrackingService {

    private final TrackingRepo trackingRepo;
    private final OrderRepo orderRepo;
    private final OrderMapper orderMapper;
    private final TrackingMapper mapper;

    private final TrackingHistoryRepo historyRepo;


    @Transactional
    public TrackingResponseDTO startTracking(UUID orderId, String location) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ValidationException("Order not found with id: " + orderId));

        OrderTrackingEntity tracking = OrderTrackingEntity.builder()
                .order(order)
                .currentStatus(TrackingStatus.ORDERED) // default status
                .location(location != null ? location : "Warehouse")
                .build();
        tracking = trackingRepo.save(tracking);

        saveHistory(tracking, TrackingStatus.ORDERED, location);
        return orderMapper.toTrackingResponseDTO(trackingRepo.save(tracking));
    }



    @Transactional
    public TrackingResponseDTO updateTracking(UUID orderId, TrackingStatus status, String location) {
        OrderTrackingEntity tracking = trackingRepo.findAll().stream()
                .filter(t -> t.getOrder().getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Tracking not found for order id: " + orderId));

        if (status != null) tracking.setCurrentStatus(status);
        if (location != null) tracking.setLocation(location);
        tracking = trackingRepo.save(tracking);

        saveHistory(tracking, status, location);

        return orderMapper.toTrackingResponseDTO(trackingRepo.save(tracking));
    }
    public void saveHistory(OrderTrackingEntity tracking, TrackingStatus status, String location) {
        OrderTrackingHistoryEntity history = OrderTrackingHistoryEntity.builder()
                .tracking(tracking)
                .status(status)
                .location(location)
                .updatedAt(Instant.now())
                .build();
        historyRepo.save(history);
    }


    public List<TrackingHistoryDTO> getTrackingHistory(UUID orderId) {
        OrderTrackingEntity tracking = trackingRepo.findByOrderId(orderId)
                .orElseThrow(() -> new ValidationException("Tracking not found"));
        return historyRepo.findByTrackingIdOrderByUpdatedAtAsc(tracking.getId())
                .stream()
                .map(mapper::toHistoryDTO)
                .toList();
    }

}
