package com.projetos.biblioteca.relatorio;

import com.projetos.biblioteca.emprestimo.Emprestimo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmprestimoAtrasadoResponseDTO(
        Long id,
        String livroTitulo,
        String usuarioNome,
        LocalDate dataPrevistaDevolucao,
        long diasAtraso,
        BigDecimal multaEstimada
) {
    public static EmprestimoAtrasadoResponseDTO de(Emprestimo e, BigDecimal multaPorDia) {
        long diasAtraso = e.diasDeAtraso(LocalDate.now());
        BigDecimal multa = multaPorDia.multiply(BigDecimal.valueOf(diasAtraso));

        return new EmprestimoAtrasadoResponseDTO(
                e.getId(), e.getExemplar().getLivro().getTitulo(),
                e.getUsuario().getNome(), e.getDataPrevistaDevolucao(),
                e.diasDeAtraso(LocalDate.now()), multa
        );
    }
}
