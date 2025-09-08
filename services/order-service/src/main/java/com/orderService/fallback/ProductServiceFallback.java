package com.orderService.fallback;

import com.orderService.dto.request.ReserveRequestDTO;
import com.orderService.dto.response.ProductResponseDTO;
import com.orderService.feign.ProductServiceClient;
import com.orderService.utils.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Component
@Slf4j
public class ProductServiceFallback implements ProductServiceClient {
    @Override
    public ResponseEntity<GenericResponse<ProductResponseDTO>> getProductById(String id) {
        return ResponseEntity.ok(
                GenericResponse.<ProductResponseDTO>builder()
                        .message("Product service unavailable. Returning fallback response.")
                        .statusCode("503")
                        .success(false)
                        .timestamp(Timestamp.from(Instant.now()))
                        .errors(Map.of("productId", id, "error", "Unable to fetch product details"))
                        .data(null)
                        .build()
        );
    }

    @Override
    public void reserveStock(ReserveRequestDTO reserveRequestDTO) {
        log.warn("Fallback: unable to reserve stock with reservationID {}." , reserveRequestDTO.getReservationId());
    }

    @Override
    public void releaseStock(String reservationId) {
        log.warn("Fallback: unable to release stock for reservation {}." , reservationId);
    }

    @Override
    public void confirmReservation(String reservationId) {
        log.warn("Fallback triggered: Failed to confirm reservation for reservationId {}.", reservationId);
    }
}
