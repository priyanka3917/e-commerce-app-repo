package com.userService.mapper;

import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetAllUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
import com.userService.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //CREATE USER
    UserEntity toEntity(UserCreateRequestDTO dto);
    UserCreateResponseDTO toResponse(UserEntity entity);

    //GET ALL USER
    List<GetAllUsersResponseDTO> getAllResponse(List<UserEntity> entities);

    //GET OR UPDATE USER
    GetOrUpdateUserByIdResponseDTO getOrUpdateUserByIdResponse(UserEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserResponse(UpdateUserRequestDTO request, @MappingTarget UserEntity entity);
}
