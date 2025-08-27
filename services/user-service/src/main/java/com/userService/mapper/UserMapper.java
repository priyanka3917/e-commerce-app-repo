package com.userService.mapper;

import com.userService.dto.request.UpdateUserRequestDTO;
import com.userService.dto.request.UserCreateRequestDTO;
import com.userService.dto.response.GetAllUsersResponseDTO;
import com.userService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.userService.dto.response.UserCreateResponseDTO;
import com.userService.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

//    @Mapping(target = "username", expression = "java(dto.username())")
//    @Mapping(target = "fullName", expression = "java(dto.fullName())")
//    @Mapping(target = "email", expression = "java(dto.email())")
//    @Mapping(target = "address", expression = "java(dto.address())")
//    @Mapping(target = "password", expression = "java(dto.password())")
    //CREATE USER
    UserEntity toEntity(UserCreateRequestDTO dto);
    UserCreateResponseDTO toResponse(UserEntity entity);

    //GET ALL USER
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "address", target = "address"),
            @Mapping(source = "fullName", target = "fullName"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "updatedAt", target = "updatedAt")
    })
    List<GetAllUsersResponseDTO> getAllResponse(List<UserEntity> entities);

    //GET OR UPDATE USER
    GetOrUpdateUserByIdResponseDTO getOrUpdateUserByIdResponse(UserEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserResponse(UpdateUserRequestDTO request, @MappingTarget UserEntity entity);

}
