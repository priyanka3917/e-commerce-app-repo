package com.userService.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserCreateRequestDTO(

        @Size(max=100)
        String username,

        @Email
        String email,

        String address,

        String password,

        @Size(max=150)
        String fullname
) {
}
