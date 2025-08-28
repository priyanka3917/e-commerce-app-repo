package com.orderService.repository;

import com.orderService.entity.OrderTrackingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingHistoryRepo extends JpaRepository<OrderTrackingHistoryEntity, UUID> {
    List<OrderTrackingHistoryEntity> findByTrackingIdOrderByUpdatedAtAsc(UUID trackingId);
}
