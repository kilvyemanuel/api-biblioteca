package com.projetos.biblioteca.reserva;

import java.time.Instant;

public record ReservaResponseDTO(
        Long id,
        Long livroId,
        String tituloLivro,
        StatusReserva status,
        Instant criadoEm
) {
    public static ReservaResponseDTO de(Reserva r) {
        return new ReservaResponseDTO(
            r.getId(), r.getLivro().getId(), r.getLivro().getTitulo(),
            r.getStatus(), r.getCriadoEm()
        );
    }
}
