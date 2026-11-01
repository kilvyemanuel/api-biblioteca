CREATE TABLE reserva (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL REFERENCES usuario(id),
    livro_id        BIGINT NOT NULL REFERENCES livro(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    criado_em       TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT ck_reserva_status
                     CHECK ( status IN ('ATIVA', 'ATENDIDA', 'CANCELADA') )
);

CREATE UNIQUE INDEX idx_reserva_usuario_livro_ativa_unico
ON reserva(usuario_id, livro_id)
WHERE status = 'ATIVA';
