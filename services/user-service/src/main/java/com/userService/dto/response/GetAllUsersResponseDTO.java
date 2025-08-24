package com.userService.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record GetAllUsersResponseDTO(
        UUID id,
        String username,
        String email,
        String address,
        String fullName,
        Instant createdAt,
        Instant UpdatedAt
) {
}
