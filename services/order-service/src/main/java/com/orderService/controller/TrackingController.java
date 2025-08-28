package com.orderService.controller;

import com.orderService.dto.request.TrackingCreateRequestDTO;
import com.orderService.dto.request.TrackingUpdateRequestDTO;
import com.orderService.dto.response.TrackingHistoryDTO;
import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.service.TrackingService;
import com.orderService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

  private final TrackingService trackingService;

    @Operation(summary = "Start tracking for an order")
    @PostMapping
    public ResponseEntity<GenericResponse<TrackingResponseDTO>> startTracking(
            @Valid @RequestBody TrackingCreateRequestDTO request) {
        TrackingResponseDTO response = trackingService.startTracking(request.orderId(), request.location());
        return ResponseEntity.ok(GenericResponse.success(response));
    }

    @Operation(summary = "Update tracking status for an order")
    @PutMapping
    public ResponseEntity<GenericResponse<TrackingResponseDTO>> updateTracking(
            @Valid @RequestBody TrackingUpdateRequestDTO request) {
        TrackingResponseDTO response = trackingService.updateTracking(
                request.orderId(),
                request.status(),
                request.location()
        );
        return ResponseEntity.ok(GenericResponse.success(response));
    }

    @Operation(summary = "Get full tracking history for an order")
    @GetMapping("/{orderId}/history")
    public ResponseEntity<GenericResponse<List<TrackingHistoryDTO>>> getTrackingHistory(@PathVariable UUID orderId) {
        List<TrackingHistoryDTO> history = trackingService.getTrackingHistory(orderId);
        return ResponseEntity.ok(GenericResponse.success(history));
    }
}
