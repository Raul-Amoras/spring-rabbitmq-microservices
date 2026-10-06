package com.ms.user.mappers;

public interface BaseMapper<ENT, REQ, RES> {
    ENT toModel(REQ requestDto);

    RES toResponseDto(ENT entity);
}
