package com.productService.entity;

import com.productService.enums.ReservationStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "stock_reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "unique_reservation_product", def = "{'reservationId' : 1, 'productId': 1}", unique = true)
public class StockReservationEntity {
    @Id
    private String reservationId;
    private String productId;
    private int quantity;
    private Instant reservedAt;
    private ReservationStatus status; // RESERVED, RELEASED
}

