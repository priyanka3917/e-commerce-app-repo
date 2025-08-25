package com.orderService.repository;

import com.orderService.entity.OrderEntity;
import com.orderService.entity.OrderTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackingRepo extends JpaRepository<OrderTrackingEntity, UUID> {
}
