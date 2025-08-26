package com.userService.dto.response;

import java.time.Instant;

public record GetOrUpdateUserByIdResponseDTO(
        String username,
        String email,
        String address,
        String fullName,
        Instant createdAt,
        Instant updatedAt
) {
}
