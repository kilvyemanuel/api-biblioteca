CREATE TABLE usuario (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    email         VARCHAR(180) NOT NULL UNIQUE,
    senha_hash    VARCHAR(100) NOT NULL,
    papel         VARCHAR(20) NOT NULL DEFAULT 'MEMBRO',
    ativo         BOOLEAN NOT NULL DEFAULT 'TRUE',
    criado_em     TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_usuario_papel
                     CHECK (papel IN ('MEMBRO', 'BIBLIOTECARIO', 'ADMIN'))
)
