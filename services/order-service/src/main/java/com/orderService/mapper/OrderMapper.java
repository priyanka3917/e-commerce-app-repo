package com.orderService.mapper;

import com.orderService.dto.response.OrderItemResponseDTO;
import com.orderService.dto.response.OrderResponseDTO;
import com.orderService.dto.response.PaymentResponseDTO;
import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.entity.OrderItemEntity;
import com.orderService.entity.OrderTrackingEntity;
import com.orderService.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponseDTO toOrderResponseDTO(OrderEntity order) {
        if (order == null) {
            return null;
        }

        // Map list of OrderItem entities to OrderItemResponseDTOs
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(this::toOrderItemResponseDTO)
                .collect(Collectors.toList());

        // Map Tracking entity to TrackingResponseDTO
        TrackingResponseDTO trackingDTO = toTrackingResponseDTO(order.getTracking());

        // Map Payment entity to PaymentResponseDTO
        PaymentResponseDTO paymentDTO = toPaymentResponseDTO(order.getPayment());

        return new OrderResponseDTO(
                order.getId(), // Assuming your entity ID is a UUID string
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(), // Assuming OrderStatus is an enum
                order.getOrderDate(),
                itemDTOs,
                trackingDTO,
                paymentDTO
        );
    }

    private OrderItemResponseDTO toOrderItemResponseDTO(OrderItemEntity item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }

    private TrackingResponseDTO toTrackingResponseDTO(OrderTrackingEntity tracking) {
        if (tracking == null) return null;
        return new TrackingResponseDTO(
                tracking.getId(),
                tracking.getCurrentStatus(),
                tracking.getLocation(),
                tracking.getLastUpdated()
        );
    }

    private PaymentResponseDTO toPaymentResponseDTO(PaymentEntity payment) {
        if (payment == null) return null;
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionId(),
                payment.getAmount(),
                payment.getPaymentDate()
        );
    }
}
