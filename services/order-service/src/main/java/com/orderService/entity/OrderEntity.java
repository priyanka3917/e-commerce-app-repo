package com.orderService.entity;

import com.orderService.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true,nullable = false)
    private UUID id;

    @Column(nullable =false)
    private UUID userId;

    @Column(nullable =false,precision = 10,scale=2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable =false)
    private OrderStatus status; // PENDING,PAID,SHIPPED,DELIVERED

    @CreationTimestamp
    private Instant orderDate;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentEntity payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderTrackingEntity tracking;

    @PrePersist
    void prePersist() {
        if (orderDate == null) orderDate = Instant.now();
        if (status == null) status = OrderStatus.PENDING;
        if (totalPrice == null) totalPrice = BigDecimal.ZERO;
    }
}
