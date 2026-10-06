package com.ms.email.services;

import com.ms.email.enums.StatusEmail;
import com.ms.email.models.EmailModel;
import com.ms.email.repositories.EmailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmailService {
    private final EmailRepository emailRepository;
    private final JavaMailSender emailSender;
    private final String emailOrigem;

    public EmailService(EmailRepository emailRepository, JavaMailSender emailSender, @Value(value = "${spring.mail.username}") String emailOrigem) {
        this.emailRepository = emailRepository;
        this.emailSender = emailSender;
        this.emailOrigem = emailOrigem;
    }



    public void enviarEmail(EmailModel emailModel){
           emailModel.setDataEnvio(LocalDateTime.now());
           emailModel.setEmailOrigem(emailOrigem);

        try {
           SimpleMailMessage mensagem = new SimpleMailMessage();
           mensagem.setTo(emailModel.getEmailDestino());
           mensagem.setSubject(emailModel.getAssunto());
           mensagem.setText(emailModel.getMensagem());
           emailSender.send(mensagem);

           emailModel.setStatusEmail(StatusEmail.ENVIADO);
       } catch (MailException e) {
            log.error("Erro ao enviar e-mail para: {}. Motivo: {}", emailModel.getEmailDestino(), e.getMessage());
            emailModel.setStatusEmail(StatusEmail.ERROR);
       }
           emailRepository.save(emailModel);
    }
}
