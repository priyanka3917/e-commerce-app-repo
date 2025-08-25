package com.orderService.repository;

import com.orderService.entity.OrderEntity;
import com.orderService.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepo extends JpaRepository<PaymentEntity, UUID> {
}
