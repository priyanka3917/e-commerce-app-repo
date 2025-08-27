package com.orderService.dto.request;

import com.orderService.enums.OrderStatus;
import com.orderService.enums.TrackingStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TrackingUpdateRequestDTO(
        @NotNull UUID orderId,
        @NotNull TrackingStatus status,
        String location
) {
}
