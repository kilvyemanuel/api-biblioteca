package com.projetos.biblioteca.usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String emai,
        Papel papel
) {
    public static UsuarioResponseDTO de(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(), u.getNome(), u.getEmail(), u.getPapel()
        );
    }
}
