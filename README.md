# Biblioteca API

![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F)
![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17-336791)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

API REST para gerenciamento de uma biblioteca (catálogo, exemplares, empréstimos e reservas), com autenticação JWT e controle de acesso por papéis.

## Tecnologias

- Java 21 e Spring Boot 4.1 (Web, Data JPA, Validation, Security)
- PostgreSQL 17 com migrações Flyway
- Autenticação stateless com JWT (jjwt) e senhas com BCrypt
- Documentação OpenAPI com Swagger UI (springdoc)
- Docker e Docker Compose

## Requisitos

- Docker e Docker Compose
- JDK 21 (apenas para rodar fora do Docker)

## Como rodar

### Com Docker

```bash
docker compose up --build db app
```

| Serviço | Endereço |
|---|---|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| PostgreSQL | localhost:5432 |

### Pela IDE ou terminal

```bash
docker compose up -d db
cd back-end
./mvnw spring-boot:run        # no Windows: mvnw.cmd spring-boot:run
```

### Primeiro acesso

O cadastro (`POST /auth/registro`) sempre cria um usuário com papel `MEMBRO`. Para criar o primeiro administrador, promova-o direto no banco:

```bash
docker compose exec db psql -U biblioteca -d biblioteca \
  -c "UPDATE usuario SET papel = 'ADMIN' WHERE email = 'seu@email.com';"
```

Depois, faça login em `POST /auth/login` e envie o token no cabeçalho `Authorization: Bearer <token>`.

## Documentação da API

Com a aplicação rodando, a documentação interativa fica em `/swagger-ui/index.html` e a especificação OpenAPI em `/v3/api-docs` (pode ser importada no Insomnia ou Postman).

Os erros seguem o formato `ProblemDetail` (RFC 9457).

## Estrutura

```
.
├── docker-compose.yml
├── back-end/
│   ├── Dockerfile
│   ├── docs/                  especificação e diagramas
│   └── src/main/
│       ├── java/com/projetos/biblioteca/
│       │   ├── auth/          login, registro, JWT e filtro de autenticação
│       │   ├── usuario/  categoria/  autor/  livro/  exemplar/
│       │   ├── emprestimo/  reserva/  relatorio/
│       │   ├── config/        segurança, CORS e propriedades
│       │   └── exception/     tratamento global de erros
│       └── resources/db/migration/   migrações Flyway (V1 a V5)
└── front-end/                 interface React
```

Cada módulo segue o fluxo Controller, Service e Repository, e as respostas da API usam DTOs em vez de entidades.
