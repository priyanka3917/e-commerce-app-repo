package com.userService.dto.request;

import com.userService.constants.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserCreateRequestDTO(

        @Size(max =100)
        String userName,

        @Email
        String email,

        String address,

        String password,

        @Size(max= 100)
        String fullName,

        RoleType roleName
) {
}
