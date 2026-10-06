package com.ms.user.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
        @JsonProperty("nome") // Diz para o Spring: "Quando receber um JSON, leia o campo 'nome' e coloque nesta variável"
        @NotBlank String name,

        @NotBlank @Email String email) {
}
