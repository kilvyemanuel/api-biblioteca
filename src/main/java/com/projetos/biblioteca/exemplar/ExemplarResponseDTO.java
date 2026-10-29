package com.projetos.biblioteca.exemplar;

public record ExemplarResponseDTO(
        Long id,
        Long livroId,
        String codigo,
        StatusExemplar status
) {
    public static ExemplarResponseDTO de(Exemplar e) {
        return new ExemplarResponseDTO(
                e.getId(), e.getLivro().getId(),
                e.getCodigo(), e.getStatus()
        );
    }
}
