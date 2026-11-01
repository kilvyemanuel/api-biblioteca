package com.projetos.biblioteca.relatorio;

public record TopLivroResponseDTO(
        Long livroId,
        String livroTitulo,
        Long total
) {}
