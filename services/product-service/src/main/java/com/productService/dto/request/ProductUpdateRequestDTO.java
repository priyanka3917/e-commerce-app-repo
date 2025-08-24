package com.productService.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.Map;

public record ProductUpdateRequestDTO(
        String description,

        @DecimalMin("0.0")
        BigDecimal price,

        @Min(0)
        Integer stock,

        Map<String, Object> attributes
) {
}
