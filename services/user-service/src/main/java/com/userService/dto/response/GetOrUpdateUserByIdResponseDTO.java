package com.userService.dto.response;

import java.time.Instant;

public record GetOrUpdateUserByIdResponseDTO(
        String userName,
        String email,
        String address,
        String fullName,
        String roleName,
        Instant createdAt,
        Instant updatedAt
) {
}
