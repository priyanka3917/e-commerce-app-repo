package com.userService.controllers;

import com.userService.dto.request.LoginUserRequestDTO;
import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetAllUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
import com.userService.exception.ValidationException;
import com.userService.service.UserService;
import com.userService.service.impl.UserDetailsServiceImpl;
import com.userService.utils.GenericResponse;
import com.userService.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/sign-up")
    @Operation(summary = "User signUp")
    public ResponseEntity<GenericResponse<UserCreateResponseDTO>> signUp(@Valid @RequestBody UserCreateRequestDTO requestDTO) {
        UserCreateResponseDTO responseDTO = userService.createUser(requestDTO);
        return ResponseEntity.ok(GenericResponse.success(responseDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "User Login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserRequestDTO requestDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.userName(), requestDTO.password())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(requestDTO.userName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occur while createAuthenticationToken", e);
            return new ResponseEntity<>("Incorrect userName or password", HttpStatus.BAD_REQUEST);
        }
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