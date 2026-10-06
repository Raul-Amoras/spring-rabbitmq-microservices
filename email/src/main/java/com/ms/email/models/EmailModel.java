package com.ms.email.models;

import com.ms.email.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "TB_EMAIL")
public class EmailModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID emailId;
    private UUID userId;
    private String emailOrigem;
    private String emailDestino;
    private String assunto;
    @Column(columnDefinition = "TEXT")
    private String mensagem;
    private LocalDateTime dataEnvio;
    private StatusEmail statusEmail;

}
