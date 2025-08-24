package com.orderService.entity;

import com.orderService.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_tracking")
public class OrderTrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true,nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private OrderEntity order;

    @Column(nullable = false)
    private OrderStatus currentStatus; //PLACED,SHIPPED,DELIVERED

    private String location;
    private String trackingNumber;

    @UpdateTimestamp
    private Instant lastUpdated;

    @PrePersist
    void prePersist() {
        if (lastUpdated == null) lastUpdated = Instant.now();
    }
}
