package com.userService.service;

import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetAllUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserCreateResponseDTO createUser(UserCreateRequestDTO user);
    List<GetAllUsersResponseDTO> getAllUser();
    GetOrUpdateUserByIdResponseDTO getUserById(UUID id);
    GetOrUpdateUserByIdResponseDTO updateUserById(UpdateUserRequestDTO requestDTO);
    String deleteUserById(UUID id);


}
