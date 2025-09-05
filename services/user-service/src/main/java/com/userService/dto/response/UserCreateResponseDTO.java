package com.userService.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserCreateResponseDTO(
        UUID id,
        String userName,
        String email,
        String address,
        String fullName,
        String roleName,
        Instant createdAt
) {
}
