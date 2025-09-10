package com.orderService.controller;


import com.orderService.dto.request.PaymentCreateRequestDTO;
import com.orderService.dto.response.GetUsersResponseDTO;
import com.orderService.dto.response.PaymentResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.feign.UserServiceClient;
import com.orderService.repository.OrderRepo;
import com.orderService.service.PaymentService;
import com.orderService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderRepo orderRepo;
    private final UserServiceClient userServiceClient;

    @Operation(summary = "Make a payment for an order")
    @PostMapping
    public ResponseEntity<GenericResponse<PaymentResponseDTO>> makePayment(
            @Valid @RequestBody PaymentCreateRequestDTO request) {

        OrderEntity order = orderRepo.findById(request.orderId())
                .orElseThrow(() -> new ValidationException("Order not found with id: " + request.orderId()));
        // RBAC check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            GetUsersResponseDTO currentUser = Objects.requireNonNull(userServiceClient
                            .getUserDetailByUsername(currentUsername)
                            .getBody())
                    .getData();

            if (!currentUser.id().equals(order.getUserId())) {
                throw new AccessDeniedException("You are not authorized to pay for this order.");
            }
        }

        PaymentResponseDTO response = paymentService.makePayment(order, request.amount(), request.paymentMethod());
        return ResponseEntity.ok(GenericResponse.success(response));
    }
    @Operation(summary = "Get payment details by order ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<GenericResponse<PaymentResponseDTO>> getPaymentByOrderId(@PathVariable UUID orderId) {
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ValidationException("Order not found with id: " + orderId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            GetUsersResponseDTO currentUser = Objects.requireNonNull(userServiceClient
                            .getUserDetailByUsername(currentUsername)
                            .getBody())
                    .getData();

            if (!currentUser.id().equals(order.getUserId())) {
                throw new AccessDeniedException("You are not authorized to view this payment.");
            }
        }

        PaymentResponseDTO response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(GenericResponse.success(response));
    }
}

