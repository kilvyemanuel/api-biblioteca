package com.projetos.biblioteca.auth;

import java.time.Instant;

public record TokenResponseDTO(
        String token,
        String tipo,
        Instant expiraEm
) {}
