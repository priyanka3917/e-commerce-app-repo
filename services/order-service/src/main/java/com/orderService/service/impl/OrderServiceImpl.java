package com.orderService.service.impl;

import com.orderService.dto.request.OrderCreateRequestDTO;
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
import jakarta.validation.executable.ValidateOnExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
    OrderServiceImpl(UserServiceClient userServiceClient, ProductServiceClient productServiceClient,OrderRepo orderRepo,OrderItemRepo orderItemRepo,OrderMapper orderMapper){
        this.userServiceClient=userServiceClient;
        this.productServiceClient=productServiceClient;
        this.orderRepo=orderRepo;
        this.orderItemRepo=orderItemRepo;
        this.orderMapper=orderMapper;
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderCreateRequestDTO request) {
        UUID userId = request.userId();
        //Validate user, call user service
        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> user= userServiceClient.getUserById(userId);
        if(user.getBody().getData() == null){
            throw new ValidationException("User not found with id: " + userId);
        }
        OrderEntity order = new OrderEntity();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItemEntity> orderItems = request.items().stream().map(itemRequest -> {
            //Fetch Product details using Feign
            ResponseEntity<GenericResponse<ProductResponseDTO>> productResponse = productServiceClient.getProductById(itemRequest.productId());
            if (productResponse.getBody() == null || productResponse.getBody().getData().stock() < itemRequest.quantity()) {
                throw new RuntimeException("Product not available or insufficient stock: " + itemRequest.productId());
            }
            ProductResponseDTO product = productResponse.getBody().getData();
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProductId(product.id());
            orderItem.setQuantity(product.stock());
            orderItem.setUnitPrice(product.price());
            orderItem.setOrder(order);
            orderItemRepo.save(orderItem);
            return orderItem;
        }).collect(Collectors.toList());
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);
        orderRepo.save(order);
        return orderMapper.toOrderResponseDTO(order);
    }
}
