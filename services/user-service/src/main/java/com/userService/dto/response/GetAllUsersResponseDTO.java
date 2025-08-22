package com.userService.dto.response;

import java.time.Instant;
import java.util.UUID;

public record GetAllUsersResponseDTO(
        UUID id,
        String username,
        String email,
        String address,
        String fullname,
        Instant createdAt,
        Instant updatedAt

) {
}
