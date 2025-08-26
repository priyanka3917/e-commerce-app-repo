package com.orderService.dto.request;

import com.orderService.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TrackingUpdateRequestDTO(
        @NotNull UUID orderId,
        @NotNull OrderStatus status,
        String location
) {
}
