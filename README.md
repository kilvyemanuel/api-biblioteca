# Biblioteca

Este repositório contém o sistema completo de biblioteca, organizado em duas partes:

- `back-end`: API REST em Java + Spring Boot.
- `front-end`: aplicação web em React + Vite.
- `docker-compose.yml`: orquestra o banco PostgreSQL, a API e o frontend localmente.

## Estrutura

```text
biblioteca/
├── back-end/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── front-end/
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml
├── README.md
└── .gitignore
```

## Executando com Docker

```bash
docker compose up --build
```

- API: http://localhost:8080
- Frontend: http://localhost:5173
- PostgreSQL: localhost:5432

## Executando localmente

### Backend

```bash
cd back-end
./mvnw spring-boot:run
```

### Frontend

```bash
cd front-end
npm install
npm run dev
```

## Verificações úteis

```bash
cd back-end && ./mvnw test
cd front-end && npm test -- --run
```

## Observações

- O backend usa PostgreSQL e Flyway para migrações.
- O frontend pode consumir a API configurada pelo ambiente local ou pela tela de configurações.
- Arquivos gerados e caches (como `node_modules`, `target`, `.idea`) devem permanecer fora do controle de versão.
