package com.orderService.controller;

import com.orderService.dto.request.TrackingCreateRequestDTO;
import com.orderService.dto.request.TrackingUpdateRequestDTO;
import com.orderService.dto.response.GetUsersResponseDTO;
import com.orderService.dto.response.TrackingHistoryDTO;
import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.feign.UserServiceClient;
import com.orderService.repository.OrderRepo;
import com.orderService.service.TrackingService;
import com.orderService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
public class TrackingController {

  private final TrackingService trackingService;
    private final UserServiceClient userServiceClient;
    private final OrderRepo orderRepo;


    @Operation(summary = "Start tracking for an order")
    @PostMapping
    public ResponseEntity<GenericResponse<TrackingResponseDTO>> startTracking(
            @Valid @RequestBody TrackingCreateRequestDTO request) {
        // RBAC: Only admin or owner of the order
        checkOrderAccess(request.orderId());

        TrackingResponseDTO response = trackingService.startTracking(request.orderId(), request.location());
        return ResponseEntity.ok(GenericResponse.success(response));
    }

    @Operation(summary = "Update tracking status for an order")
    @PutMapping
    public ResponseEntity<GenericResponse<TrackingResponseDTO>> updateTracking(
            @Valid @RequestBody TrackingUpdateRequestDTO request) {
        // RBAC: Only admin or owner of the order
        checkOrderAccess(request.orderId());

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
        // RBAC: Only admin or owner of the order
        checkOrderAccess(orderId);

        List<TrackingHistoryDTO> history = trackingService.getTrackingHistory(orderId);
        return ResponseEntity.ok(GenericResponse.success(history));
    }

    private void checkOrderAccess(UUID orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new AccessDeniedException("Order not found with id: " + orderId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            GetUsersResponseDTO currentUser = userServiceClient
                    .getUserDetailByUsername(currentUsername)
                    .getBody()
                    .getData();

            if (!currentUser.id().equals(order.getUserId())) {
                throw new AccessDeniedException("You are not authorized to access this order tracking.");
            }
        }
    }
}

