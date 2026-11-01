package com.projetos.biblioteca.livro;

import com.projetos.biblioteca.autor.Autor;

import java.util.List;

public record LivroResumoResponseDTO(
        Long id,
        String titulo,
        String isbn,
        String categoria,
        List<String> autores,
        long exemplaresDisponiveis
) {
    public static LivroResumoResponseDTO de(Livro l, long exemplaresDisponiveis) {
        return new LivroResumoResponseDTO(
                l.getId(), l.getTitulo(), l.getIsbn(),
                l.getCategoria().getNome(),
                l.getAutores().stream().map(Autor::getNome).toList(),
                exemplaresDisponiveis
        );
    }
}
