package com.projetos.biblioteca.autor;

public record AutorResponseDTO(
        Long id,
        String nome,
        String nacionalidade
) {
    public static AutorResponseDTO de(Autor a) {
        return new AutorResponseDTO(
                a.getId(), a.getNome(), a.getNacionalidade()
        );
    }
}
