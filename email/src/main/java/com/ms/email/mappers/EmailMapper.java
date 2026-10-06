package com.ms.email.mappers;

import com.ms.email.dtos.request.EmailRequestDto;
import com.ms.email.models.EmailModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailMapper extends BaseMapper<EmailModel, EmailRequestDto, EmailRequestDto>{
}
