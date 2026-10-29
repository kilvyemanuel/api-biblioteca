package com.projetos.biblioteca.livro;

import com.projetos.biblioteca.autor.AutorResponseDTO;
import com.projetos.biblioteca.categoria.CategoriaResponseDTO;

import java.util.List;

public record LivroDetalheResponseDTO(
        Long id,
        String titulo,
        String isbn,
        Short anoPublicacao,
        String sinopse,
        CategoriaResponseDTO categoria,
        List<AutorResponseDTO> autores,
        long totalExemplares,
        long exemplaresDisponiveis
) {
    public static LivroDetalheResponseDTO de(Livro l, long totalExemplares, long exemplaresDisponiveis) {
        return new LivroDetalheResponseDTO(
                l.getId(), l.getTitulo(), l.getIsbn(), l.getAnoPublicacao(),
                l.getSinopse(), CategoriaResponseDTO.de(l.getCategoria()),
                l.getAutores().stream().map(AutorResponseDTO::de).toList(),
                totalExemplares, exemplaresDisponiveis
        );
    }
}
