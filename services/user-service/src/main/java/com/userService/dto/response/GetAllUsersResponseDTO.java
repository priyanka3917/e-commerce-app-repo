package com.userService.dto.response;

import java.time.Instant;
import java.util.UUID;

public record GetAllUsersResponseDTO(
        UUID id,
        String userName,
        String email,
        String address,
        String fullName,
        Instant createdAt,
        Instant updatedAt
) {
}
