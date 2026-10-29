package com.projetos.biblioteca.livro;

public record LivroFiltroDTO(
        String titulo,
        Long autorId,
        Long categoriaId
) {}
