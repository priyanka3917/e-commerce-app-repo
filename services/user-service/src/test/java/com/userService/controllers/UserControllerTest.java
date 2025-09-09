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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService userService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    UserDetailsServiceImpl userDetailsService;
    @Mock
    JwtUtil jwtUtil;
    @InjectMocks
    UserController userController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void signUp_success() {
        UserCreateRequestDTO req = mock(UserCreateRequestDTO.class);
        UserCreateResponseDTO resp = mock(UserCreateResponseDTO.class);
        when(userService.createUser(req)).thenReturn(resp);
        ResponseEntity<GenericResponse<UserCreateResponseDTO>> result = userController.signUp(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody().getData());
    }

    @Test
    void login_success() {
        LoginUserRequestDTO req = mock(LoginUserRequestDTO.class);
        when(req.userName()).thenReturn("user1");
        when(req.password()).thenReturn("pass");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        ResponseEntity<String> result = userController.login(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("jwt-token", result.getBody());
    }

    @Test
    void getAllUser_success() {
        List<GetUsersResponseDTO> users = List.of(mock(GetUsersResponseDTO.class));
        when(userService.getAllUser()).thenReturn(users);
        ResponseEntity<GenericResponse<List<GetUsersResponseDTO>>> result = userController.getAllUser();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(users, result.getBody().getData());
    }

    @Test
    void getUserById_admin_success() {
        UUID id = UUID.randomUUID();

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(userService.getUserDetailByUsername("admin")).thenReturn(currentUser);

        GetOrUpdateUserByIdResponseDTO resp = mock(GetOrUpdateUserByIdResponseDTO.class);
        when(userService.getUserById(id)).thenReturn(resp);

        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> result = userController.getUserById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody().getData());
    }

    @Test
    void getUserById_nonAdmin_self_success() {
        UUID id = UUID.randomUUID();
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities(); SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(currentUser.id()).thenReturn(id);
        when(userService.getUserDetailByUsername("user1")).thenReturn(currentUser);
        GetOrUpdateUserByIdResponseDTO resp = mock(GetOrUpdateUserByIdResponseDTO.class);
        when(userService.getUserById(id)).thenReturn(resp);
        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> result = userController.getUserById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody().getData());
    }

    @Test
    void getUserById_nonAdmin_other_throws() {
        UUID id = UUID.randomUUID();
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(currentUser.id()).thenReturn(UUID.randomUUID());
        when(userService.getUserDetailByUsername("user1")).thenReturn(currentUser);
        assertThrows(AccessDeniedException.class, () -> userController.getUserById(id));
    }

    @Test
    void updateUserById_admin_success() {
        UpdateUserRequestDTO req = mock(UpdateUserRequestDTO.class);
        UUID id = UUID.randomUUID();
        when(req.id()).thenReturn(id);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(userService.getUserDetailByUsername("admin")).thenReturn(currentUser);
        GetOrUpdateUserByIdResponseDTO resp = mock(GetOrUpdateUserByIdResponseDTO.class);
        when(userService.updateUserById(req)).thenReturn(resp);
        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> result = userController.updateUserById(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody().getData());
    }

    @Test
    void updateUserById_nonAdmin_self_success() {
        UpdateUserRequestDTO req = mock(UpdateUserRequestDTO.class);
        UUID id = UUID.randomUUID();
        when(req.id()).thenReturn(id);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(currentUser.id()).thenReturn(id);
        when(userService.getUserDetailByUsername("user1")).thenReturn(currentUser);
        GetOrUpdateUserByIdResponseDTO resp = mock(GetOrUpdateUserByIdResponseDTO.class);
        when(userService.updateUserById(req)).thenReturn(resp);
        ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> result = userController.updateUserById(req);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody().getData());
    }

    @Test
    void updateUserById_nonAdmin_other_throws() {
        UpdateUserRequestDTO req = mock(UpdateUserRequestDTO.class);
        UUID id = UUID.randomUUID();
        when(req.id()).thenReturn(id);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(currentUser.id()).thenReturn(UUID.randomUUID());
        when(userService.getUserDetailByUsername("user1")).thenReturn(currentUser);
        assertThrows(AccessDeniedException.class, () -> userController.updateUserById(req));
    }

    @Test
    void getUserDetailByUsername_admin_success() {
        String userName = "user1";
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(userService.getUserDetailByUsername("admin")).thenReturn(currentUser);
        GetUsersResponseDTO resp = mock(GetUsersResponseDTO.class);
        when(userService.getUserDetailByUsername(userName)).thenReturn(resp);
        ResponseEntity<GenericResponse<GetUsersResponseDTO>> result = userController.getUserDetailByUsername(userName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody().getData());
    }

    @Test
    void getUserDetailByUsername_nonAdmin_self_success() {
        String userName = "user1";
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(currentUser.userName()).thenReturn(userName);
        when(userService.getUserDetailByUsername("user1")).thenReturn(currentUser);
        GetUsersResponseDTO resp = mock(GetUsersResponseDTO.class);
        when(userService.getUserDetailByUsername(userName)).thenReturn(resp);
        ResponseEntity<GenericResponse<GetUsersResponseDTO>> result = userController.getUserDetailByUsername(userName);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resp, result.getBody().getData());
    }

    @Test
    void getUserDetailByUsername_nonAdmin_other_throws() {
        String userName = "otherUser";
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(auth).getAuthorities();SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        GetUsersResponseDTO currentUser = mock(GetUsersResponseDTO.class);
        when(currentUser.userName()).thenReturn("user1");
        when(userService.getUserDetailByUsername("user1")).thenReturn(currentUser);
        assertThrows(AccessDeniedException.class, () -> userController.getUserDetailByUsername(userName));
    }

    @Test
    void deleteUserById_success() {
        UUID id = UUID.randomUUID();
        when(userService.deleteUserById(id)).thenReturn("deleted");
        ResponseEntity<GenericResponse<String>> result = userController.deleteUserById(id);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("deleted", result.getBody().getData());
    }
}
