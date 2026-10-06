package com.ms.user.dtos.events;

import com.ms.user.models.UserModel;

public record UserCreatedEvent(UserModel userModel) {
}
