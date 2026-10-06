# Microsserviços com Spring Boot e RabbitMQ

Projeto de estudo com **dois microsserviços** Java/Spring Boot que se comunicam de forma **assíncrona** através de um **broker RabbitMQ**.

- **user** – cadastra usuários e, após o commit no banco, publica uma mensagem na fila pedindo o envio de um e-mail de boas-vindas.
- **email** – consome a mensagem da fila, envia o e-mail via SMTP (Gmail) e registra o resultado (`ENVIADO` ou `ERROR`) no banco.

## Arquitetura

"

```
 Cliente HTTP
      │  POST /users/
      ▼
┌───────────────────┐   mensagem JSON    ┌──────────────┐   consome   ┌────────────────────┐
│  ms-user (8081)   │ ─────────────────► │   RabbitMQ   │ ──────────► │  ms-email (8082)   │
│  Postgres ms_user │  fila default.email│              │             │  Postgres ms_email │
└───────────────────┘                    └──────────────┘             └─────────┬──────────┘
                                                                                │ SMTP
                                                                                ▼
                                                                          E-mail do usuário
```

### Fluxo

1. `POST /users/` recebe `nome` e `email` (validados com Bean Validation).
2. `UserService.save` (transacional) salva o usuário e publica um `UserCreatedEvent` via `ApplicationEventPublisher`.
3. `UserCreatedEventListener` usa `@TransactionalEventListener(AFTER_COMMIT)` + `@Async`: a mensagem só é enviada **depois do commit**, sem bloquear a resposta HTTP.
4. `UserProducer` envia um `EmailResponseDto` (JSON) para a _default exchange_ usando o nome da fila (`default.email`) como routing key.
5. No serviço de e-mail, `EmailConsumer` (`@RabbitListener`) recebe o `EmailRequestDto`, converte para `EmailModel` (MapStruct) e chama `EmailService.enviarEmail`.
6. `EmailService` envia o e-mail com `JavaMailSender`, define o status (`ENVIADO`/`ERROR`) e persiste o registro na tabela `TB_EMAIL`.

## Tecnologias

| Item       | Detalhe                                                                             |
| ---------- | ----------------------------------------------------------------------------------- |
| Linguagem  | Java 21                                                                             |
| Framework  | Spring Boot 4.1.1 (Web MVC, Data JPA, AMQP, Validation, Mail)                       |
| Mensageria | RabbitMQ (`spring-boot-starter-amqp`), conversor JSON `JacksonJsonMessageConverter` |
| Banco      | PostgreSQL                                                                          |
| Migrations | Flyway (somente no `user`)                                                          |
| Mapeamento | MapStruct 1.5.5 + Lombok                                                            |
| Build      | Maven (wrapper `mvnw` incluso em cada módulo)                                       |

## Estrutura

```
microservices/
├── user/                         # Produtor
│   └── src/main
│       ├── java/com/ms/user
│       │   ├── configs/          # RabbitMQConfig (conversor JSON)
│       │   ├── controllers/      # UserController (POST /users/)
│       │   ├── services/         # UserService
│       │   ├── listeners/        # UserCreatedEventListener (AFTER_COMMIT, async)
│       │   ├── producers/        # UserProducer (envia para o RabbitMQ)
│       │   ├── dtos/             # request, response e events
│       │   ├── mappers/          # MapStruct
│       │   ├── models/           # UserModel (TB_USERS)
│       │   └── repositories/
│       └── resources/
│           ├── application.properties
│           ├── docker-compose.yaml   # PostgreSQL
│           └── db/migration/V1__create_table_users.sql
└── email/                        # Consumidor
    └── src/main/java/com/ms/email
        ├── configs/              # RabbitMQConfig (declara a fila + conversor JSON)
        ├── consumer/             # EmailConsumer (@RabbitListener)
        ├── services/             # EmailService (envio SMTP + persistência)
        ├── models/               # EmailModel (TB_EMAIL)
        ├── enums/                # StatusEmail
        ├── dtos/ mappers/ repositories/
```

## Pré-requisitos

- JDK 21
- Docker (para o PostgreSQL)
- Uma instância do RabbitMQ (local ou em nuvem, ex.: CloudAMQP)
- Conta Gmail com **senha de app** para o envio de e-mails

## Configuração

### Variáveis de ambiente

| Variável        | Serviço | Descrição                                                            |
| --------------- | ------- | -------------------------------------------------------------------- |
| `RABBITMQ_KEY`  | user    | URI de conexão do RabbitMQ, ex.: `amqp://guest:guest@localhost:5672` |
| `RABBITMQ_KEY`  | email   | Mesma URI do RabbitMQ                                                |
| `MAIL_USER`     | email   | Endereço Gmail usado como remetente                                  |
| `MAIL_PASSWORD` | email   | Senha de app do Gmail                                                |

### Portas

| Serviço           | Porta |
| ----------------- | ----- |
| user              | 8081  |
| email             | 8082  |
| PostgreSQL (host) | 5434  |

### Bancos de dados

O `docker-compose.yaml` do módulo `user` sobe um PostgreSQL (container `java-postgres-ms`, porta `5434`) já com o banco `ms_user`. O serviço `email` usa o banco `ms_email` **na mesma instância**, que precisa ser criado manualmente:

```bash
docker compose -f user/src/main/resources/docker-compose.yaml up -d
docker exec -it java-postgres-ms psql -U postgres -c "CREATE DATABASE ms_email;"
```

- `user`: schema versionado pelo Flyway (`V1__create_table_users.sql`) com `ddl-auto=validate`.
- `email`: tabela criada automaticamente pelo Hibernate (`ddl-auto=update`).

## Como executar

Com o Postgres e o RabbitMQ no ar e as variáveis definidas (exemplo PowerShell):

```powershell
$env:RABBITMQ_KEY = "amqp://guest:guest@localhost:5672"
$env:RABBITMQ_KEYRABBITMQ_KEY = $env:RABBITMQ_KEY
$env:MAIL_USER = "seu-email@gmail.com"
$env:MAIL_PASSWORD = "sua-senha-de-app"

# terminal 1
cd email
.\mvnw spring-boot:run

# terminal 2
cd user
.\mvnw spring-boot:run
```

> Inicie o **email** primeiro: é ele quem declara a fila `default.email` (o `user` apenas publica nela).

## Testando

```bash
curl -X POST http://localhost:8081/users/ \
  -H "Content-Type: application/json" \
  -d '{"nome": "Maria", "email": "maria@exemplo.com"}'
```

Resposta (`201 Created`):

```json
{ "userId": "b1c2...-uuid", "name": "Maria", "email": "maria@exemplo.com" }
```

Em seguida, o serviço `email` recebe a mensagem, envia o e-mail "Cadastro realizado com sucesso!" e grava o registro em `TB_EMAIL`. O painel do RabbitMQ (porta 15672, se local) permite acompanhar a fila `default.email`.

> Note que o campo de entrada do JSON se chama `nome` (via `@JsonProperty`), enquanto a resposta usa `name`.

## Contrato da mensagem

Mensagem JSON publicada na fila `default.email`:

```json
{
  "userId": "uuid",
  "emailDestino": "maria@exemplo.com",
  "assunto": "Cadastro realizado com sucesso!",
  "mensagem": "Maria, seja bem vindo...."
}
```

## Decisões de design

- **Consistência**: o envio só acontece após o commit (`AFTER_COMMIT`), evitando e-mails para usuários que não chegaram a ser salvos.
- **Desacoplamento**: o `user` não conhece o `email`; só compartilha o nome da fila e o formato JSON.
- **Fila durável** declarada pelo consumidor (`new Queue(name, true)`).
- **Resiliência básica**: falhas de SMTP são capturadas e registradas com status `ERROR`, sem derrubar o consumidor.

## Possíveis melhorias

- Adicionar um `docker-compose` completo (Postgres + RabbitMQ + criação do `ms_email`).
- Dead Letter Queue e política de retry para e-mails com erro.
- Declarar a fila também no `user` para não depender da ordem de inicialização.
- Evento `UserPasswordChangedEvent` já existe, mas ainda não é utilizado.
- Testes de integração (os existentes são apenas `contextLoads`).
