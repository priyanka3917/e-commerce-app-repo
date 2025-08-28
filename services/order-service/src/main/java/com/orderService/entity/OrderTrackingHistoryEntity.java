package com.orderService.entity;


import com.orderService.enums.TrackingStatus;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tracking_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTrackingHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", nullable = false)
    private OrderTrackingEntity tracking;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TrackingStatus status;

    private String location;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (updatedAt == null) updatedAt = Instant.now();
    }
}
