package com.orderService.dto.response;

import java.time.Instant;

public record GetOrUpdateUserByIdResponseDTO(
        String userName,
        String email,
        String address,
        String fullName,
        Instant createdAt,
        Instant UpdatedAt
) {
}
