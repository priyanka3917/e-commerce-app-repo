package com.orderService.entity;

import com.orderService.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true,nullable = false)
    private UUID id;

    @OneToOne(mappedBy = "order_id")
    private OrderEntity order;

    private BigDecimal amount;
    private String transactionId;
    private PaymentStatus paymentStatus;

    @CreatedDate
    private Instant paymentDate;


}
