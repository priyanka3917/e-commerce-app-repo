package com.orderService.controller;

import com.orderService.dto.request.OrderCreateRequestDTO;
import com.orderService.dto.response.GetUsersResponseDTO;
import com.orderService.dto.response.OrderResponseDTO;
import com.orderService.feign.UserServiceClient;
import com.orderService.service.OrderService;
import com.orderService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    private UserServiceClient userServiceClient;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Create Order.")
    public ResponseEntity<GenericResponse<OrderResponseDTO>> createOrder(@Valid @RequestBody OrderCreateRequestDTO req) {
        return ResponseEntity.ok(GenericResponse.success(orderService.createOrder(req)));
    }

    @GetMapping("/{id}/{offset}/{size}")
    @Operation(summary = "Get paginated order details for a particular user")
    public ResponseEntity<GenericResponse<Page<OrderResponseDTO>>> getOrderDetailsOfUser(
            @PathVariable UUID id,@PathVariable int offset, @PathVariable int size ){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        //  Call UserService via Feign/RestTemplate to resolve userId from username
        GetUsersResponseDTO userDto = Objects.requireNonNull(userServiceClient
                        .getUserDetailByUsername(currentUsername)
                        .getBody())           // unwrap ResponseEntity
                .getData();          // unwrap GenericResponse

        UUID currentUserId = userDto.id();

        if (!isAdmin && !currentUserId.equals(id)) {
            throw new AccessDeniedException("You are not authorized to view these orders.");
        }


        Page<OrderResponseDTO> pagedOrders = orderService.getOrdersDetailByUserId(id, offset,size);
        return ResponseEntity.ok(GenericResponse.success(pagedOrders));
    }

}
