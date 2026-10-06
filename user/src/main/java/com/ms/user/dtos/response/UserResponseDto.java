package com.ms.user.dtos.response;

import java.util.UUID;

public record UserResponseDto(UUID userId, String name, String email) {
}
