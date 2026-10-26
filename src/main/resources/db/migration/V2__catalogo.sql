CREATE TABLE categoria (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(80) NOT NULL UNIQUE,
    descricao       VARCHAR(255)
);

CREATE TABLE autor (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    nacionalidade   VARCHAR(80)
);

CREATE TABLE livro (
    id              BIGSERIAL PRIMARY KEY,
    titulo          VARCHAR(200) NOT NULL,
    isbn            VARCHAR(17) NOT NULL UNIQUE,
    ano_publicacao  SMALLINT,
    sinopse         TEXT,
    categoria_id    BIGINT NOT NULL REFERENCES categoria(id) ON DELETE RESTRICT,
    criado_em       TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_ano_publicacao
                   CHECK ( ano_publicacao > 0 )
);

CREATE TABLE livro_autor (
    livro_id        BIGINT REFERENCES livro(id) ON DELETE CASCADE,
    autor_id        BIGINT REFERENCES autor(id) ON DELETE RESTRICT,
    PRIMARY KEY (livro_id, autor_id)
);

CREATE INDEX idx_livro_categoria ON livro(categoria_id);
CREATE INDEX idx_livro_titulo_lower ON livro(lower(titulo));