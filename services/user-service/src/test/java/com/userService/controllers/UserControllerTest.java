package com.userService.controllers;

import com.userService.constants.RoleType;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private GetUsersResponseDTO userResponse;
    private GetOrUpdateUserByIdResponseDTO userByIdResponse;
    private UserCreateResponseDTO userCreateResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userResponse = new GetUsersResponseDTO(
                userId,
                "testuser",
                "test@example.com",
                "123 Street",
                "Test User",
                "USER",
                Instant.now(),
                Instant.now()
        );

        userByIdResponse = new GetOrUpdateUserByIdResponseDTO(
                "testuser",
                "test@example.com",
                "123 Street",
                "Test User",
                "USER",
                Instant.now(),
                Instant.now()
        );

        userCreateResponse = new UserCreateResponseDTO(
                userId,
                "testuser",
                "test@example.com",
                "123 Street",
                "Test User",
                "USER",
                Instant.now()
        );

        SecurityContextHolder.setContext(securityContext);

    }

    // ✅ sign-up
    @Test
    void testSignUp() {

        UserCreateRequestDTO requestDTO =
                new UserCreateRequestDTO(
                        "testuser",
                        "pass",
                        "test@example.com",
                        "123 Street",
                        "Test User",
                        RoleType.USER);

        when(userService.createUser(requestDTO)).thenReturn(userCreateResponse);

        ResponseEntity<GenericResponse<UserCreateResponseDTO>> response = userController.signUp(requestDTO);

        assertEquals("testuser", response.getBody().getData().userName());
    }

    // ✅ login
    @Test
    void testLogin() {
        LoginUserRequestDTO requestDTO = new LoginUserRequestDTO(
                "testuser", "pass");

        UserDetails userDetails = User.withUsername("testuser")
                .password("pass")
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mockJwt");

        ResponseEntity<String> response = userController.login(requestDTO);

        assertEquals("mockJwt", response.getBody());
    }

    // ✅ get all users (ADMIN only)
    @Test
    void testGetAllUser_AsAdmin() {
       // when(securityContext.getAuthentication()).thenReturn(authentication);
       // when(authentication.getName()).thenReturn("testuser");
        //doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();

        when(userService.getAllUser()).thenReturn(List.of(userResponse));

        ResponseEntity<GenericResponse<List<GetUsersResponseDTO>>> response = userController.getAllUser();

        assertEquals(1, response.getBody().getData().size());
    }

    // ✅ get user by id (as ADMIN)
    @Test
    void testGetUserById_AsAdmin() {
        // Add stubbing here instead
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();
        when(userService.getUserDetailByUsername("testuser")).thenReturn(userResponse);
        when(userService.getUserById(userId)).thenReturn(userByIdResponse);

        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> response =
                userController.getUserById(userId);

        assertEquals("testuser", response.getBody().getData().userName());
    }
    // ❌ Negative: Non-admin accessing another user's id
    @Test
    void testGetUserById_AsNonAdminAccessDenied() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

        when(userService.getUserDetailByUsername("testuser")).thenReturn(userResponse);

        assertThrows(AccessDeniedException.class,
                () -> userController.getUserById(UUID.randomUUID()));
    }

    // ✅ update user as self
    @Test
    void testUpdateUser_AsSelf() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UpdateUserRequestDTO updateDTO =
                new UpdateUserRequestDTO(userId, "testuser", "new@example.com", "123 Street", "Test User", "pass");

        when(authentication.getName()).thenReturn("testuser");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();
        when(userService.getUserDetailByUsername("testuser")).thenReturn(userResponse);
        when(userService.updateUserById(updateDTO)).thenReturn(userByIdResponse);

        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> response =
                userController.updateUserById(updateDTO);

        assertEquals("testuser", response.getBody().getData().userName());
    }

    // ❌ Negative: Non-admin updating another user
    @Test
    void testUpdateUser_AsNonAdminAccessDenied() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        UpdateUserRequestDTO updateDTO =
                new UpdateUserRequestDTO(UUID.randomUUID(), "otheruser", "other@example.com", "456 Street", "Other User", "pass");

        when(authentication.getName()).thenReturn("testuser");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();
        when(userService.getUserDetailByUsername("testuser")).thenReturn(userResponse);

        assertThrows(AccessDeniedException.class,
                () -> userController.updateUserById(updateDTO));
    }

    // ✅ get user by username (as self)
    @Test
    void testGetUserDetailByUsername_AsSelf() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();
        when(userService.getUserDetailByUsername("testuser")).thenReturn(userResponse);

        ResponseEntity<GenericResponse<GetUsersResponseDTO>> response =
                userController.getUserDetailByUsername("testuser");

        assertEquals("testuser", response.getBody().getData().userName());
    }

    // ❌ Negative: Non-admin trying to fetch another username
    @Test
    void testGetUserDetailByUsername_AsNonAdminAccessDenied() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();
        when(userService.getUserDetailByUsername("testuser")).thenReturn(userResponse);

        assertThrows(AccessDeniedException.class,
                () -> userController.getUserDetailByUsername("otheruser"));
    }

    // ✅ delete user (ADMIN only)
    @Test
    void testDeleteUserById_AsAdmin() {
       // when(securityContext.getAuthentication()).thenReturn(authentication);
       // when(authentication.getName()).thenReturn("admin");
        // doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))).when(authentication).getAuthorities();

        when(userService.deleteUserById(userId)).thenReturn("User Deleted Successfully " + userId);

        ResponseEntity<GenericResponse<String>> response =
                userController.deleteUserById(userId);

        assertTrue(response.getBody().getData().contains("User Deleted Successfully"));
    }
}
