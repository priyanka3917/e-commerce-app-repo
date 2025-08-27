package com.orderService.controller;

import com.orderService.dto.request.OrderCreateRequestDTO;
import com.orderService.dto.response.OrderResponseDTO;
import com.orderService.service.OrderService;
import com.orderService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
        return ResponseEntity.ok(GenericResponse.success(orderService.createOrder(req)));
    }

    @GetMapping
    @Operation(summary = "Get paginated order details for a particular user")
    public ResponseEntity<GenericResponse<Page<OrderResponseDTO>>> getOrderDetailsOfUser(
            @RequestParam UUID userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderResponseDTO> pagedOrders = orderService.getOrdersDetailByUserId(userId, pageable);
        return ResponseEntity.ok(GenericResponse.success(pagedOrders));
    }

}
