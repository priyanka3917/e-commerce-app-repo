package com.orderService.repository;

import com.orderService.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, UUID> {
    Optional<List<OrderEntity>> findByUserId(UUID id);
    Page<OrderEntity> findByUserId(UUID userId, Pageable pageable);
}
