package com.userService.service;

import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserCreateResponseDTO createUser(UserCreateRequestDTO user);
    List<GetUsersResponseDTO> getAllUser();
    GetUsersResponseDTO getUserDetailByUsername(String usereName);
    GetOrUpdateUserByIdResponseDTO getUserById(UUID id);
    GetOrUpdateUserByIdResponseDTO updateUserById(UpdateUserRequestDTO requestDTO);
    String deleteUserById(UUID id);


}
