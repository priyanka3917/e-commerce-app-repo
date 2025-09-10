package com.userService.service.impl;

import com.userService.constants.RoleType;
import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
import com.userService.entity.UserEntity;
import com.userService.exception.ValidationException;
import com.userService.mapper.UserMapper;
import com.userService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        createdAt = Instant.now();
        updatedAt = createdAt.plusSeconds(60); // just an example

        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserName("testUser");
        userEntity.setEmail("test@example.com");
        userEntity.setAddress("123 Test Street");
        userEntity.setFullName("Test User");
        userEntity.setRoleName(RoleType.USER.name());
        userEntity.setPassword("hashedpassword");
        userEntity.setCreatedAt(createdAt);
        userEntity.setUpdatedAt(updatedAt);
    }

    @Test
    void createUser_success() {
        UserCreateRequestDTO request = new UserCreateRequestDTO(
                "testUser",
                "test@example.com",
                "123 Test Street",
                "password123",
                "Test User",
                RoleType.USER
        );

        UserCreateResponseDTO responseDto = new UserCreateResponseDTO(
                userId,
                "testUser",
                "test@example.com",
                "123 Test Street",
                "Test User",
                "USER",
                createdAt
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(userMapper.toResponse(userEntity)).thenReturn(responseDto);

        UserCreateResponseDTO response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("testUser", response.userName());
        assertEquals("USER", response.roleName());
        verify(userRepository).save(userEntity);
    }

    @Test
    void createUser_emailAlreadyExists() {
        UserCreateRequestDTO request = new UserCreateRequestDTO(
                "testUser",
                "test@example.com",
                "123 Test Street",
                "password123",
                "Test User",
                RoleType.USER
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ValidationException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void getAllUser_success() {
        List<UserEntity> users = List.of(userEntity);
        List<GetUsersResponseDTO> dtoList = List.of(
                new GetUsersResponseDTO(
                        userId,
                        "testUser",
                        "test@example.com",
                        "123 Test Street",
                        "Test User",
                        "USER",
                        createdAt,
                        updatedAt
                )
        );

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.getAllResponse(users)).thenReturn(dtoList);

        List<GetUsersResponseDTO> response = userService.getAllUser();

        assertEquals(1, response.size());
        assertEquals("testUser", response.get(0).userName());
        assertEquals("USER", response.get(0).roleName());
    }

    @Test
    void getUserDetailByUsername_found() {
        GetUsersResponseDTO dto = new GetUsersResponseDTO(
                userId,
                "testUser",
                "test@example.com",
                "123 Test Street",
                "Test User",
                "USER",
                createdAt,
                updatedAt
        );

        when(userRepository.findByUserName("testUser")).thenReturn(userEntity);
        when(userMapper.getUserResponse(userEntity)).thenReturn(dto);

        GetUsersResponseDTO response = userService.getUserDetailByUsername("testUser");

        assertEquals("testUser", response.userName());
        assertEquals("USER", response.roleName());
    }

    @Test
    void getUserDetailByUsername_notFound() {
        when(userRepository.findByUserName("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserDetailByUsername("unknown"));
    }

    @Test
    void getUserById_found() {
        GetOrUpdateUserByIdResponseDTO dto = new GetOrUpdateUserByIdResponseDTO(
                "testUser",
                "test@example.com",
                "123 Test Street",
                "Test User",
                "USER",
                createdAt,
                updatedAt
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.getOrUpdateUserByIdResponse(userEntity)).thenReturn(dto);

        GetOrUpdateUserByIdResponseDTO response = userService.getUserById(userId);

        assertEquals("testUser", response.userName());
        assertEquals("USER", response.roleName());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> userService.getUserById(userId));
    }

    @Test
    void deleteUserById_success() {
        when(userRepository.existsById(userId)).thenReturn(true);

        String response = userService.deleteUserById(userId);

        assertTrue(response.contains("User Deleted Successfully"));
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserById_notFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ValidationException.class, () -> userService.deleteUserById(userId));
    }
    @Test
    void updateUserById_successPasswordUpdate() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO(
                userId,
                "testUser",
                "test@example.com",
                "newPassword123",
                "Test User",
                "USER"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        //when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.getOrUpdateUserByIdResponse(userEntity))
                .thenReturn(new GetOrUpdateUserByIdResponseDTO(
                        "testUser",
                        "test@example.com",
                        "Address",
                        "Test User",
                        "USER",
                        Instant.now(),
                        Instant.now()
                ));

        GetOrUpdateUserByIdResponseDTO response = userService.updateUserById(request);

        assertNotNull(response);
        verify(userRepository).save(userEntity);
        assertEquals("testUser", response.userName());
    }

    @Test
    void updateUserById_successEmailUpdate() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO(
                userId,
                "testUser",
                "newemail@example.com",
                null,
                "Test User",
                "USER"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.getOrUpdateUserByIdResponse(userEntity))
                .thenReturn(new GetOrUpdateUserByIdResponseDTO(
                        "testUser",
                        "newemail@example.com",
                        "Address",
                        "Test User",
                        "USER",
                        Instant.now(),
                        Instant.now()
                ));

        GetOrUpdateUserByIdResponseDTO response = userService.updateUserById(request);

        assertEquals("newemail@example.com", response.email());
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUserById_emailAlreadyExists_throwsException() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO(
                userId,
                "testUser",
                "existing@example.com",
                null,
                "Test User",
                "USER"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(ValidationException.class, () -> userService.updateUserById(request));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void updateUserById_userNotFound_throwsException() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO(
                userId,
                "testUser",
                "test@example.com",
                "password123",
                "Test User",
                "USER"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> userService.updateUserById(request));
    }

}
