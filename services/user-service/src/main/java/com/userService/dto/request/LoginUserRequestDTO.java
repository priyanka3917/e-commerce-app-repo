package com.userService.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record LoginUserRequestDTO(
        @Size(max =100)
        String userName,
        String password
) {
}
