package com.orderService.fallback;

import com.orderService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.orderService.dto.response.GetUsersResponseDTO;
import com.orderService.dto.response.ProductResponseDTO;
import com.orderService.feign.UserServiceClient;
import com.orderService.utils.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class UserServiceFallback implements UserServiceClient {
    @Override
    public ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> getUserById(UUID id) {
        log.error("User service failed for userId {}", id);
        return ResponseEntity.ok(
                GenericResponse.<GetOrUpdateUserByIdResponseDTO>builder()
                        .message("Service unavailable")
                        .statusCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()))
                        .success(false)
                        .timestamp(Timestamp.from(Instant.now()))
                        .errors(Map.of("fallback-triggered for user service", "Remote service failed or timed out"))
                        .data(null)
                        .build()
        );
    }
    @Override
    public ResponseEntity<GenericResponse<GetUsersResponseDTO>> getUserDetailByUsername(String username) {
        log.error("User service failed for username {}", username);
        return ResponseEntity.ok(
                GenericResponse.<GetUsersResponseDTO>builder()
                        .message("Service unavailable")
                        .statusCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()))
                        .success(false)
                        .timestamp(Timestamp.from(Instant.now()))
                        .errors(Map.of("fallback-triggered for user service", "Remote service failed or timed out"))
                        .data(null)
                        .build()
        );
    }



}
