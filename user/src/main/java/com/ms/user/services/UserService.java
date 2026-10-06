package com.ms.user.services;

import com.ms.user.dtos.events.UserCreatedEvent;
import com.ms.user.models.UserModel;
import com.ms.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public UserService(UserRepository userRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public UserModel save(UserModel userModel){
        UserModel savedUser = userRepository.save(userModel);

        applicationEventPublisher.publishEvent(new UserCreatedEvent(savedUser));

        return savedUser;
    }
}
