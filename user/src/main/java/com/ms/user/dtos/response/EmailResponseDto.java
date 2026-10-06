package com.ms.user.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmailResponseDto {
    private UUID userId;
    private String emailDestino;
    private String assunto;
    private String mensagem;
}
