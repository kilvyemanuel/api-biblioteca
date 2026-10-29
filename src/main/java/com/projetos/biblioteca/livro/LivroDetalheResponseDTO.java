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
        List<AutorResponseDTO> autores
) {
    public static LivroDetalheResponseDTO de(Livro l) {
        return new LivroDetalheResponseDTO(
                l.getId(), l.getTitulo(), l.getIsbn(), l.getAnoPublicacao(),
                l.getSinopse(), CategoriaResponseDTO.de(l.getCategoria()),
                l.getAutores().stream().map(AutorResponseDTO::de).toList()
        );
    }
}
