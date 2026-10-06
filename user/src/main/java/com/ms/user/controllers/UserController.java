package com.ms.user.controllers;

import com.ms.user.dtos.request.UserRequestDto;
import com.ms.user.dtos.response.UserResponseDto;
import com.ms.user.mappers.UserMapper;
import com.ms.user.models.UserModel;
import com.ms.user.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper  userMapper;

    @PostMapping("/")
    public ResponseEntity<UserResponseDto> saveUser(@RequestBody @Valid UserRequestDto userRequestDto){
        UserModel userModel = userMapper.toModel(userRequestDto);

        UserModel savedUser = userService.save(userModel);

        UserResponseDto response = userMapper.toResponseDto(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
