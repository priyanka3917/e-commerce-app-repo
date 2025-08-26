package com.orderService.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TrackingCreateRequestDTO(
        @NotNull UUID orderId,
        String location
) {
}
