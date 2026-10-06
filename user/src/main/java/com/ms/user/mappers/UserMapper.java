package com.ms.user.mappers;

import com.ms.user.dtos.request.UserRequestDto;
import com.ms.user.dtos.response.UserResponseDto;
import com.ms.user.models.UserModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserModel, UserRequestDto, UserResponseDto>{
}
