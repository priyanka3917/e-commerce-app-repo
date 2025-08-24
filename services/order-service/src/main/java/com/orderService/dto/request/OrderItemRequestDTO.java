package com.orderService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDTO(

        @NotNull
        String productId,

        @NotNull @Min(1)
        Integer quantity
) {
}
