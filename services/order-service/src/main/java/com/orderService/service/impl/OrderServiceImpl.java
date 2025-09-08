package com.orderService.service.impl;

import com.orderService.dto.request.OrderCreateRequestDTO;
import com.orderService.dto.request.OrderItemRequestDTO;
import com.orderService.dto.request.ReserveItemDTO;
import com.orderService.dto.request.ReserveRequestDTO;
import com.orderService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.orderService.dto.response.OrderResponseDTO;
import com.orderService.dto.response.ProductResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.entity.OrderItemEntity;
import com.orderService.enums.OrderStatus;
import com.orderService.feign.ProductServiceClient;
import com.orderService.feign.UserServiceClient;
import com.orderService.mapper.OrderMapper;
import com.orderService.repository.OrderItemRepo;
import com.orderService.repository.OrderRepo;
import com.orderService.service.OrderService;
import com.orderService.utils.GenericResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final OrderMapper orderMapper;
    // Constructor injection for all repositories and clients

    OrderServiceImpl(UserServiceClient userServiceClient, ProductServiceClient productServiceClient, OrderRepo orderRepo, OrderItemRepo orderItemRepo, OrderMapper orderMapper) {
        this.userServiceClient = userServiceClient;
        this.productServiceClient = productServiceClient;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateRequestDTO request) {
        UUID userId = request.userId();

        // Step 1: Validate user
        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> userResponse = userServiceClient.getUserById(userId);
        if (userResponse.getBody() == null || userResponse.getBody().getData() == null) {
            throw new ValidationException("User not found with id: " + userId);
        }

        // Step 2: Create initial order
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setReservationId("RSV-" + UUID.randomUUID().toString().substring(0, 8));

        order = orderRepo.save(order);
        final OrderEntity savedOrder = order;

        List<OrderItemEntity> orderItems = new ArrayList<>();
        List<ReserveItemDTO> reserveItems = new ArrayList<>();
        try {
            for (OrderItemRequestDTO itemRequest : request.items()) {
                // Step 3: Fetch product details
                ResponseEntity<GenericResponse<ProductResponseDTO>> productResponse =
                        productServiceClient.getProductById(itemRequest.productId());

                if (productResponse.getBody() == null || productResponse.getBody().getData() == null) {
                    throw new RuntimeException("Product not found: " + itemRequest.productId());
                }

                ProductResponseDTO product = productResponse.getBody().getData();

                if (product.stock() < itemRequest.quantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + itemRequest.productId());
                }

                // Prepare for bulk reservation
                reserveItems.add(new ReserveItemDTO(product.id(), itemRequest.quantity()));

                // Step 5: Create order item
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setProductId(product.id());
                orderItem.setQuantity(itemRequest.quantity());
                orderItem.setUnitPrice(product.price());
                orderItem.setOrder(savedOrder);
                orderItemRepo.save(orderItem);
                orderItems.add(orderItem);
            }

            // Step 4: Call bulk reservation once
            ReserveRequestDTO reserveRequest = new ReserveRequestDTO(order.getReservationId(), reserveItems);
            productServiceClient.reserveStock(reserveRequest);


            // Step 6: Finalize order
            BigDecimal totalAmount = orderItems.stream()
                    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            order.setTotalAmount(totalAmount);
            order.setItems(orderItems);
            order.setStatus(OrderStatus.PENDING); // still pending, will confirm after payment
            orderRepo.save(order);
            
            return orderMapper.toOrderResponseDTO(order);

        } catch (Exception e) {
            // Compensate: release stock if reservation failed
            try {
                productServiceClient.releaseStock(order.getReservationId());
            } catch (Exception ex) {
                log.warn("Failed to release stock for reservation {}: {}", order.getReservationId(), ex.getMessage(), ex);
            }

            order.setStatus(OrderStatus.FAILED);
            orderRepo.save(order);

            throw e;
        }
    }

    public ResponseEntity<GenericResponse<Object>> getUserFallback(UUID userId, Throwable t) {
        log.error("User service failed for userId {}: {}", userId, t.getMessage());
        return ResponseEntity.ok(
                GenericResponse.<Object>builder()
                        .message("Service unavailable: " + t.getMessage())
                        .statusCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()))
                        .success(false)
                        .timestamp(Timestamp.from(Instant.now()))
                        .errors(Map.of("fallback-triggered", "Remote service failed or timed out"))
                        .data(null)
                        .build()
        );
    }

    public ResponseEntity<GenericResponse<ProductResponseDTO>> getProductFallback(String id, Throwable t) {
        log.error("Fallback triggered: Failed to fetch product with ID {}. Reason: {}", id, t.getMessage());

        return ResponseEntity.ok(
                GenericResponse.<ProductResponseDTO>builder()
                        .message("Product service unavailable")
                        .statusCode("503")
                        .success(false)
                        .timestamp(Timestamp.from(Instant.now()))
                        .errors(Map.of("productId", id, "error", "Unable to fetch product details"))
                        .data(null)
                        .build()
        );
    }

    public void reserveStockFallback(String productId, int quantity, String reservationId, Throwable t) {
        log.warn("Fallback triggered: Failed to reserve stock for productId {} with reservationId {}. Reason: {}",
                productId, reservationId, t.getMessage());
        throw new RuntimeException("Stock reservation failed for productId: " + productId);
    }

    public void releaseStockFallback(String reservationId, Throwable t) {
        log.warn("Fallback triggered: Failed to release stock for reservationId {}. Reason: {}", reservationId, t.getMessage());
    }

    public void confirmReservationFallback(String reservationId, Throwable t) {
        log.warn("Fallback triggered: Failed to confirm reservation for reservationId {}. Reason: {}", reservationId, t.getMessage());
    }

    @Override
    public Page<OrderResponseDTO> getOrdersDetailByUserId(UUID id, int offset, int size) {
        Pageable pageable = PageRequest.of(offset, size);
        Page<OrderEntity> orders = orderRepo.findByUserId(id, pageable);

        if (orders.isEmpty()) {
            throw new ValidationException("No orders found for the id: " + id);
        }
        return orders.map(orderMapper::toOrderResponseDTO);
    }
}
