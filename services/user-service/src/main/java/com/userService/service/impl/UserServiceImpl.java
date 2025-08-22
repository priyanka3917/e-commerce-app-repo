package com.userService.service.impl;

import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetAllUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
import com.userService.entity.UserEntity;
import com.userService.exception.ValidationException;
import com.userService.mapper.UserMapper;
import com.userService.repository.UserRepository;
import com.userService.service.UserService;
import com.userService.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Override
    public UserCreateResponseDTO createUser(UserCreateRequestDTO user) {
        if(userRepository.existsByEmail(user.email()))
            throw new ValidationException("Email already exists." + user.email());
        UserEntity userEntity = userMapper.toEntity(user);
        userEntity.setPassword(PasswordUtils.hash(user.password()));
        userRepository.save(userEntity);
        return userMapper.toResponse(userEntity);
    }

    @Override
    public List<GetAllUsersResponseDTO> getAllUser() {
        return userMapper.getAllResponse(userRepository.findAll());
    }

    @Override
    public GetOrUpdateUserByIdResponseDTO getUserById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("No user found with id: " + id));
        return userMapper.getOrUpdateUserByIdResponse(user);
    }

    @Override
    public GetOrUpdateUserByIdResponseDTO updateUserById(UpdateUserRequestDTO requestDTO) {
        UserEntity user = userRepository.findById(requestDTO.id())
                .orElseThrow(() -> new ValidationException("No user found with id: " + requestDTO.id()));

        userMapper.updateUserResponse(requestDTO,user);

        //Update password
        if(requestDTO.password() != null && !requestDTO.password().isBlank()){
            user.setPassword(PasswordUtils.hash(requestDTO.password()));
        }

        //Update email
        if(requestDTO.email() !=null &&  !requestDTO.email().equalsIgnoreCase(user.getEmail())){
            if(userRepository.existsByEmail(requestDTO.email())){
                throw  new ValidationException("Email already exits: "+ requestDTO.email());
            }
        }
        user = userRepository.save(user);
        return userMapper.getOrUpdateUserByIdResponse(user);
    }

    @Override
    public String deleteUserById(UUID id) {
        if(!userRepository.existsById(id)){
            throw new ValidationException("User not found with id: "+ id);
        }
        userRepository.deleteById(id);
        return "User Deleted Successfully " + id;
    }
}
