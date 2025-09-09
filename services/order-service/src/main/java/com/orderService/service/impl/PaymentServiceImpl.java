package com.orderService.service;

import com.orderService.dto.response.PaymentResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.entity.PaymentEntity;
import com.orderService.enums.OrderStatus;
import com.orderService.enums.PaymentMethod;
import com.orderService.enums.PaymentStatus;
import com.orderService.feign.ProductServiceClient;
import com.orderService.mapper.OrderMapper;
import com.orderService.repository.OrderRepo;
import com.orderService.repository.PaymentRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final OrderRepo orderRepo;

    @Transactional
    @CircuitBreaker(name = "orderServiceCB", fallbackMethod = "fallbackPayment")
    @Retry(name = "paymentRetry")
    public PaymentResponseDTO makePayment(OrderEntity order, BigDecimal amount, PaymentMethod method) {
        // Fetch existing payment for this order (idempotent behavior)
        PaymentEntity payment = paymentRepo.findByOrderId(order.getId())
                .orElse(PaymentEntity.builder()
                        .order(order)
                        .build());

        // If already SUCCESS, return existing (idempotent)
        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return orderMapper.toPaymentResponseDTO(payment);
        }

        boolean success = simulatePaymentSuccess();
        PaymentStatus status = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        // Update existing payment or set new details
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(status);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
        payment.setPaymentDate(Instant.now());

        // Save and return
        payment = paymentRepo.save(payment);

        // If payment successful → confirm reservation
        if (status == PaymentStatus.SUCCESS) {
            try {
                productServiceClient.confirmReservation(order.getReservationId());
                order.setStatus(OrderStatus.CONFIRMED);
            } catch (Exception e) {
                log.error("Payment succeeded but failed to confirm stock: {}", e.getMessage(), e);
                throw new RuntimeException("Payment succeeded but stock confirmation failed!");
            }
        } else {
            try {
                productServiceClient.releaseStock(order.getReservationId());
                 order.setStatus(OrderStatus.FAILED);
            } catch (Exception e) {
                log.warn("Payment failed but stock release failed for reservation {}", order.getReservationId(), e);
            }
        }

        orderRepo.save(order);
        return orderMapper.toPaymentResponseDTO(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByOrderId(UUID orderId) {
        return paymentRepo.findAll().stream()
                .filter(payment -> payment.getOrder().getId().equals(orderId))
                .findFirst()
                .map(orderMapper::toPaymentResponseDTO)
                .orElseThrow(() -> new RuntimeException("Payment not found for orderId: " + orderId));
    }

    public PaymentResponseDTO fallbackPayment(OrderEntity order, BigDecimal amount, PaymentMethod method, Throwable t) {
        log.warn("Fallback triggered for payment: {}", t.getMessage());
        PaymentEntity failedEntity = PaymentEntity.builder()
                .order(order)
                .amount(amount)
                .paymentMethod(method)
                .paymentStatus(PaymentStatus.FAILED)
                .transactionId("TXN-FAILED-" + UUID.randomUUID().toString().substring(0, 8))
                .paymentDate(Instant.now())
                .build();

        paymentRepo.save(failedEntity);
        return orderMapper.toPaymentResponseDTO(failedEntity);
    }
    @Transactional
    public PaymentResponseDTO cancelPayment(UUID orderId) {
        PaymentEntity payment = paymentRepo.findByOrderId(orderId)
                .orElseThrow(()-> new ValidationException("Payment not found for orderId: "+ orderId));
        OrderEntity order = payment.getOrder();
        // Update payment status
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setPaymentDate(Instant.now());
        paymentRepo.save(payment);

        // Release stock
        productServiceClient.releaseStock(order.getReservationId());

        // Mark order as FAILED
        order.setStatus(OrderStatus.FAILED);
        orderRepo.save(order);
        return orderMapper.toPaymentResponseDTO(payment);

    }
    private boolean simulatePaymentSuccess() {
        return Math.random() < 0.6;
    }
}
