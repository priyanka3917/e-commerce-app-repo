package com.orderService.entity;

import com.orderService.enums.OrderStatus;
import com.orderService.enums.TrackingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;


import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tracking")
public class OrderTrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true,nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id",nullable = false)
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(name= "current_status",nullable =false)
    private TrackingStatus currentStatus; //PLACED,SHIPPED,DELIVERED

    private String location;

    @UpdateTimestamp
    private Instant lastUpdated;

    @PrePersist
    void prePersist() {
        if (lastUpdated == null) lastUpdated = Instant.now();
    }
}
