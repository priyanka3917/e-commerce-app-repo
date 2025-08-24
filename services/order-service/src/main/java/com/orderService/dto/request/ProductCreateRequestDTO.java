package com.orderService.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

public record ProductCreateRequestDTO(
        @NotBlank
        String name,

        @Size(max=2000)
        String description,

        @NotNull @DecimalMin("0.0")
        BigDecimal price,

        @NotNull @Min(0)
        Integer stock,

        Map<String, Object> attributes
) {
}

