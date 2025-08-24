package com.orderService.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderCreateRequestDTO(

        @NotNull
        UUID userId,

        @NotEmpty @Valid
        List<OrderItemRequestDTO> items
) {
}
