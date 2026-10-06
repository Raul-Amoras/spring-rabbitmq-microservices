package com.ms.email.dtos.request;

import java.util.UUID;

public record EmailRequestDto(UUID userId, String emailDestino, String assunto, String mensagem) {
}
