package com.orderService.service;

import com.orderService.dto.response.PaymentResponseDTO;
import com.orderService.entity.OrderEntity;
import com.orderService.entity.PaymentEntity;
import com.orderService.enums.PaymentMethod;
import com.orderService.enums.PaymentStatus;
import com.orderService.mapper.OrderMapper;
import com.orderService.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;
    private final OrderMapper orderMapper;

    @Transactional
    public PaymentResponseDTO makePayment(OrderEntity order, BigDecimal amount, PaymentMethod method) {
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);

        PaymentEntity entity = PaymentEntity.builder()
                .order(order)
                .amount(amount)
                .paymentMethod(method)
                .paymentStatus(PaymentStatus.SUCCESS) // for now assume success
                .transactionId(txnId)
                .paymentDate(Instant.now())
                .build();

        return orderMapper.toPaymentResponseDTO(paymentRepo.save(entity));
    }

    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByOrderId(UUID orderId) {
        return paymentRepo.findAll().stream()
                .filter(payment -> payment.getOrder().getId().equals(orderId))
                .findFirst()
                .map(orderMapper::toPaymentResponseDTO)
                .orElseThrow(() -> new RuntimeException("Payment not found for orderId: " + orderId));
    }
}
