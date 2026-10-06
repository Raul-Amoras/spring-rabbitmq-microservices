package com.ms.user.producers;

import com.ms.user.dtos.response.EmailResponseDto;
import com.ms.user.models.UserModel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserProducer {
    final RabbitTemplate rabbitTemplate;


    public UserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${broker.queue.email.name}")
    private String routerKey;

    public void publicarMensagemEmail(UserModel userModel){
        EmailResponseDto emailResponseDto = new EmailResponseDto();

        emailResponseDto.setUserId(userModel.getUserId());
        emailResponseDto.setEmailDestino(userModel.getEmail());
        emailResponseDto.setAssunto("Cadastro realizado com sucesso!");
        emailResponseDto.setMensagem(userModel.getName() + ", seja bem vindo....");

        rabbitTemplate.convertAndSend("", routerKey, emailResponseDto);

    }
}
