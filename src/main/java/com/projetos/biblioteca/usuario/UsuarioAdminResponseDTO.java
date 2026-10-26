package com.projetos.biblioteca.usuario;

import java.time.Instant;

public record UsuarioAdminResponseDTO(
        Long id,
        String nome,
        String email,
        Papel papel,
        boolean ativo,
        Instant criadoEm
) {
    public static UsuarioAdminResponseDTO de(Usuario u) {
        return new UsuarioAdminResponseDTO(
                u.getId(), u.getNome(), u.getEmail(), u.getPapel(),
                u.isAtivo(), u.getCriadoEm()
        );
    }
}
