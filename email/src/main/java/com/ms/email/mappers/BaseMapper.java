package com.ms.email.mappers;

public interface BaseMapper<ENT, REQ, RES> {
    ENT toModel(REQ requestDto);

    RES toResponseDto(ENT entity);
}
