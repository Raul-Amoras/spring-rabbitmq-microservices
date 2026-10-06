package com.ms.user.listeners;

import com.ms.user.dtos.events.UserCreatedEvent;
import com.ms.user.models.UserModel;
import com.ms.user.producers.UserProducer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserCreatedEventListener {

    private final UserProducer userProducer;

    public UserCreatedEventListener(UserProducer userProducer) {
        this.userProducer = userProducer;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispararEmailAposSalvarUsuario(UserCreatedEvent event){
        userProducer.publicarMensagemEmail(event.userModel());
    }
}
