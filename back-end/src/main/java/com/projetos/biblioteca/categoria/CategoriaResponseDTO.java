package com.projetos.biblioteca.categoria;

public record CategoriaResponseDTO(
        Long id,
        String nome,
        String descricao
) {
    public static CategoriaResponseDTO de(Categoria c) {
        return new CategoriaResponseDTO(
                c.getId(), c.getNome(), c.getDescricao()
        );
    }
}
