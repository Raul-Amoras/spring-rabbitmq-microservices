package com.ms.email.consumer;

import com.ms.email.dtos.request.EmailRequestDto;
import com.ms.email.mappers.EmailMapper;
import com.ms.email.models.EmailModel;
import com.ms.email.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    private final EmailMapper emailMapper;
    private final EmailService emailService;

    public EmailConsumer(EmailMapper emailMapper, EmailService emailService) {
        this.emailMapper = emailMapper;
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${broker.queue.email.name}")
    public  void listenEmailQueue(@Payload EmailRequestDto emailRequestDto){
        EmailModel emailModel = emailMapper.toModel(emailRequestDto);

        emailService.enviarEmail(emailModel);
    }
}
