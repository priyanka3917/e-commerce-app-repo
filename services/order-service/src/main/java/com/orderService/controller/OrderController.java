package com.orderService.controller;

import com.orderService.dto.request.OrderCreateRequestDTO;
import com.orderService.dto.response.OrderResponseDTO;
import com.orderService.service.OrderService;
import com.orderService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create Order.")
    public ResponseEntity<GenericResponse<OrderResponseDTO>> createOrder(@Valid @RequestBody OrderCreateRequestDTO req) {
        //return ResponseEntity.ok(GenericResponse.success(orderService.createOrder(req)));
        return null;
    }
}
