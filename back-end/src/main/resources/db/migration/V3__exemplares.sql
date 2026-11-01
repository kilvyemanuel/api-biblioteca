CREATE TABLE exemplar (
    id                BIGSERIAL PRIMARY KEY,
    livro_id          BIGINT NOT NULL REFERENCES livro(id) ON DELETE RESTRICT,
    codigo            VARCHAR(30) NOT NULL UNIQUE,
    status            VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',
    criado_em         TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_exemplar_status
                      CHECK ( status IN ('DISPONIVEL', 'EMPRESTADO', 'MANUTENCAO', 'PERDIDO'))
);

CREATE INDEX idx_exemplar_livro_status ON exemplar(livro_id, status);