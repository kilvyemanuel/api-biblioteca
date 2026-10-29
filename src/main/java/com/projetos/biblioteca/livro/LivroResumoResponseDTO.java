package com.projetos.biblioteca.livro;

import com.projetos.biblioteca.autor.Autor;

import java.util.List;

public record LivroResumoResponseDTO(
        Long id,
        String titulo,
        String isbn,
        String categoria,
        List<String> autores
) {
    public static LivroResumoResponseDTO de(Livro l) {
        return new LivroResumoResponseDTO(
                l.getId(), l.getTitulo(), l.getIsbn(),
                l.getCategoria().getNome(),
                l.getAutores().stream().map(Autor::getNome).toList()
        );
    }
}
