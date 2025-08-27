package com.orderService.controller;


import com.orderService.dto.request.PaymentCreateRequestDTO;
import com.orderService.dto.response.PaymentResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.repository.OrderRepo;
import com.orderService.service.PaymentService;
import com.orderService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderRepo orderRepo;

    @Operation(summary = "Make a payment for an order")
    @PostMapping
    public ResponseEntity<GenericResponse<PaymentResponseDTO>> makePayment(
            @Valid @RequestBody PaymentCreateRequestDTO request) {
        OrderEntity order = orderRepo.findById(request.orderId())
                .orElseThrow(() -> new ValidationException("Order not found with id: " + request.orderId()));
        PaymentResponseDTO response = paymentService.makePayment(order, request.amount(), request.paymentMethod());
        return ResponseEntity.ok(GenericResponse.success(response));
    }
    @Operation(summary = "Get payment details by order ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<GenericResponse<PaymentResponseDTO>> getPaymentByOrderId(@PathVariable UUID orderId) {
        PaymentResponseDTO response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(GenericResponse.success(response));
    }
}

