package com.userService.controllers;

import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetAllUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
import com.userService.service.UserService;
import com.userService.utils.GenericResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    @Operation(summary = "Create user.")
    public ResponseEntity<GenericResponse<UserCreateResponseDTO>> createUser(@Valid @RequestBody UserCreateRequestDTO requestDTO) {
        UserCreateResponseDTO responseDTO = userService.createUser(requestDTO);
        return ResponseEntity.ok(GenericResponse.success(responseDTO));
    }

    @GetMapping
    @Operation(summary = "Get all users.")
    public ResponseEntity<GenericResponse<List<GetAllUsersResponseDTO>>> getAllUser() {
        List<GetAllUsersResponseDTO> users = userService.getAllUser();
        return ResponseEntity.ok(GenericResponse.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id.")
    public ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> getUserById(@PathVariable UUID id) {
        GetOrUpdateUserByIdResponseDTO users = userService.getUserById(id);
        return ResponseEntity.ok(GenericResponse.success(users));
    }

    @PutMapping
    @Operation(summary = "Update user by id")
    public ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> updateUserById(@Valid @RequestBody UpdateUserRequestDTO requestDTO) {
        GetOrUpdateUserByIdResponseDTO users = userService.updateUserById(requestDTO);
        return ResponseEntity.ok(GenericResponse.success(users));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    public ResponseEntity<GenericResponse<String>> deleteUserById(@PathVariable UUID id) {
        String msg = userService.deleteUserById(id);
        return ResponseEntity.ok(GenericResponse.success(msg));
    }

}