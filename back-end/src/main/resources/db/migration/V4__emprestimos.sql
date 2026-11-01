CREATE TABLE emprestimo (
    id                          BIGSERIAL PRIMARY KEY,
    usuario_id                  BIGINT NOT NULL REFERENCES usuario(id),
    exemplar_id                 BIGINT NOT NULL REFERENCES exemplar(id),
    data_emprestimo             DATE NOT NULL,
    data_prevista_devolucao     DATE NOT NULL,
    data_devolucao              DATE,
    renovacoes                  SMALLINT NOT NULL DEFAULT 0,
    multa                       NUMERIC(10, 2) NOT NULL DEFAULT 0,
    multa_paga                  BOOLEAN NOT NULL DEFAULT FALSE,
    status                      VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    criado_em                   TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT ck_emprestimo_multa
                        CHECK ( multa >= 0 ),
    CONSTRAINT ck_emprestimo_status
                        CHECK ( status IN ('ATIVO','DEVOLVIDO') )
);

CREATE INDEX idx_emprestimo_usuario_status ON emprestimo(usuario_id, status);
CREATE INDEX idx_emprestimo_status_vencimento ON emprestimo(status, data_prevista_devolucao);

CREATE UNIQUE INDEX idx_emprestimo_exemplar_ativo_unico
ON emprestimo(exemplar_id)
WHERE status = 'ATIVO';
