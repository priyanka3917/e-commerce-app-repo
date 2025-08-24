package com.productService.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record ProductResponseDTO(
        String id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Map<String, Object> attributes
) {
}
