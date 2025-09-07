package com.productService.repository;

import com.productService.entity.StockReservationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockReservationRepository extends MongoRepository<StockReservationEntity, String> {
    Optional<StockReservationEntity> findByReservationId(String  reservationId);
    Optional<StockReservationEntity> findByReservationIdAndProductId(String reservationId, String productId);
}
