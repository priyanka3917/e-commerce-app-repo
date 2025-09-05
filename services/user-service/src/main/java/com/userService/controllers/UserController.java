package com.userService.controllers;

import com.userService.dto.request.LoginUserRequestDTO;
import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.userName(), requestDTO.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(requestDTO.userName());
        String jwt = jwtUtil.generateToken(userDetails);
        return new ResponseEntity<>(jwt, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all users.")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<List<GetUsersResponseDTO>>> getAllUser() {
        List<GetUsersResponseDTO> users = userService.getAllUser();
        return ResponseEntity.ok(GenericResponse.success(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id.")
    public ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> getUserById(@PathVariable UUID id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        GetUsersResponseDTO currentUser = userService.getUserDetailByUsername(currentUsername);

        if (!isAdmin && !currentUser.id().equals(id)) {
            throw new AccessDeniedException("You are not authorized to access this user.");
        }

        GetOrUpdateUserByIdResponseDTO users = userService.getUserById(id);
        return ResponseEntity.ok(GenericResponse.success(users));
    }

    @PutMapping
    @Operation(summary = "Update user by id")
    public ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> updateUserById(@Valid @RequestBody UpdateUserRequestDTO requestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        GetUsersResponseDTO currentUser = userService.getUserDetailByUsername(currentUsername);

        if (!isAdmin && !currentUser.id().equals(requestDTO.id())) {
            throw new AccessDeniedException("You are not authorized to access this user.");
        }

        GetOrUpdateUserByIdResponseDTO users = userService.updateUserById(requestDTO);
        return ResponseEntity.ok(GenericResponse.success(users));
    }

    @GetMapping("/userName/{userName}")
    @Operation(summary = "Get user details by username")
    public ResponseEntity<GenericResponse<GetUsersResponseDTO>> getUserDetailByUsername(@PathVariable String userName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        GetUsersResponseDTO currentUser = userService.getUserDetailByUsername(currentUsername);

        if (!isAdmin && !currentUser.userName().equals(userName)) {
            throw new AccessDeniedException("You are not authorized to access this user.");
        }

        return ResponseEntity.ok(GenericResponse.success(userService.getUserDetailByUsername(userName)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<String>> deleteUserById(@PathVariable UUID id) {
        String msg = userService.deleteUserById(id);
        return ResponseEntity.ok(GenericResponse.success(msg));
    }

}