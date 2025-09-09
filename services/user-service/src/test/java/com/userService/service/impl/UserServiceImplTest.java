package com.userService.service.impl;

import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
import com.userService.entity.UserEntity;
import com.userService.exception.ValidationException;
import com.userService.mapper.UserMapper;
import com.userService.repository.UserRepository;
import com.userService.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setup() {
        // No-op, handled by MockitoExtension
    }

    @Test
    void createUser_success() {
        UserCreateRequestDTO req = mock(UserCreateRequestDTO.class);
        UserEntity entity = new UserEntity();
        UserCreateResponseDTO resp = mock(UserCreateResponseDTO.class);
        when(req.email()).thenReturn("test@example.com");
        when(req.password()).thenReturn("pass");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(req)).thenReturn(entity);
        Mockito.mockStatic(PasswordUtils.class).when(() -> PasswordUtils.hash(anyString())).thenReturn("hashed");
        when(userMapper.toResponse(entity)).thenReturn(resp);
        UserCreateResponseDTO result = userService.createUser(req);
        assertEquals(resp, result);
    }

    @Test
    void createUser_emailExists_throws() {
        UserCreateRequestDTO req = mock(UserCreateRequestDTO.class);
        when(req.email()).thenReturn("test@example.com");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        ValidationException ex = assertThrows(ValidationException.class, () -> userService.createUser(req));
        assertTrue(ex.getMessage().contains("Email already exists."));
    }

    @Test
    void getAllUser_success() {
        List<UserEntity> entities = List.of(new UserEntity());
        List<GetUsersResponseDTO> dtos = List.of(mock(GetUsersResponseDTO.class));
        when(userRepository.findAll()).thenReturn(entities);
        when(userMapper.getAllResponse(entities)).thenReturn(dtos);
        List<GetUsersResponseDTO> result = userService.getAllUser();
        assertEquals(dtos, result);
    }

    @Test
    void getUserDetailByUsername_success() {
        UserEntity entity = new UserEntity();
        GetUsersResponseDTO dto = mock(GetUsersResponseDTO.class);
        when(userRepository.findByUserName("user1")).thenReturn(entity);
        when(userMapper.getUserResponse(entity)).thenReturn(dto);
        GetUsersResponseDTO result = userService.getUserDetailByUsername("user1");
        assertEquals(dto, result);
    }

    @Test
    void getUserDetailByUsername_notFound_throws() {
        when(userRepository.findByUserName("user1")).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserDetailByUsername("user1"));
    }

    @Test
    void getUserById_success() {
        UUID id = UUID.randomUUID();
        UserEntity entity = new UserEntity();
        GetOrUpdateUserByIdResponseDTO dto = mock(GetOrUpdateUserByIdResponseDTO.class);
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userMapper.getOrUpdateUserByIdResponse(entity)).thenReturn(dto);
        GetOrUpdateUserByIdResponseDTO result = userService.getUserById(id);
        assertEquals(dto, result);
    }

    @Test
    void getUserById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> userService.getUserById(id));
    }

    @Test
    void updateUserById_success_updatePasswordAndEmail() {
        UUID id = UUID.randomUUID();
        UpdateUserRequestDTO req = mock(UpdateUserRequestDTO.class);
        UserEntity entity = new UserEntity();
        entity.setEmail("old@example.com");
        GetOrUpdateUserByIdResponseDTO dto = mock(GetOrUpdateUserByIdResponseDTO.class);
        when(req.id()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        doAnswer(inv -> {
            entity.setEmail("new@example.com");
            return null;
        }).when(userMapper).updateUserResponse(req, entity);
        when(req.password()).thenReturn("newpass");
        Mockito.mockStatic(PasswordUtils.class).when(() -> PasswordUtils.hash("newpass")).thenReturn("hashed");
        when(req.email()).thenReturn("new@example.com");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.getOrUpdateUserByIdResponse(entity)).thenReturn(dto);
        GetOrUpdateUserByIdResponseDTO result = userService.updateUserById(req);
        assertEquals(dto, result);
    }

    @Test
    void updateUserById_success_noPasswordNoEmailChange() {
        UUID id = UUID.randomUUID();
        UpdateUserRequestDTO req = mock(UpdateUserRequestDTO.class);
        UserEntity entity = new UserEntity();
        entity.setEmail("same@example.com");
        GetOrUpdateUserByIdResponseDTO dto = mock(GetOrUpdateUserByIdResponseDTO.class);
        when(req.id()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userMapper).updateUserResponse(req, entity);
        when(req.password()).thenReturn(null);
        when(req.email()).thenReturn("same@example.com");
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.getOrUpdateUserByIdResponse(entity)).thenReturn(dto);
        GetOrUpdateUserByIdResponseDTO result = userService.updateUserById(req);
        assertEquals(dto, result);
    }

    @Test
    void updateUserById_emailExists_throws() {
        UUID id = UUID.randomUUID();
        UpdateUserRequestDTO req = mock(UpdateUserRequestDTO.class);
        UserEntity entity = new UserEntity();
        entity.setEmail("old@example.com");
        when(req.id()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(userMapper).updateUserResponse(req, entity);
        when(req.password()).thenReturn(null);
        when(req.email()).thenReturn("new@example.com");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);
        ValidationException ex = assertThrows(ValidationException.class, () -> userService.updateUserById(req));
        assertTrue(ex.getMessage().contains("Email already exits"));
    }

    @Test
    void updateUserById_userNotFound_throws() {
        UUID id = UUID.randomUUID();
        UpdateUserRequestDTO req = mock(UpdateUserRequestDTO.class);
        when(req.id()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> userService.updateUserById(req));
    }

    @Test
    void deleteUserById_success() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository).deleteById(id);
        String result = userService.deleteUserById(id);
        assertTrue(result.contains("User Deleted Successfully"));
    }

    @Test
    void deleteUserById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.existsById(id)).thenReturn(false);
        ValidationException ex = assertThrows(ValidationException.class, () -> userService.deleteUserById(id));
        assertTrue(ex.getMessage().contains("User not found with id"));
    }
}

