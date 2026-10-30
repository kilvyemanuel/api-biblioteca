package com.projetos.biblioteca.emprestimo;

import com.projetos.biblioteca.exemplar.Exemplar;
import com.projetos.biblioteca.livro.Livro;
import com.projetos.biblioteca.usuario.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmprestimoResponseDTO(
        Long id,
        Long usuarioId,
        Long livroId,
        String tituloLivro,
        String codigoExemplar,
        LocalDate dataEmprestimo,
        LocalDate dataPrevistaDevolucao,
        LocalDate dataDevolucao,
        int renovacoes,
        BigDecimal multa,
        boolean multaPaga,
        StatusEmprestimo status,
        boolean atrasado
) {
    public static EmprestimoResponseDTO de(Emprestimo e) {
        Usuario u = e.getUsuario();
        Exemplar ex = e.getExemplar();
        Livro l = ex.getLivro();

        return new EmprestimoResponseDTO(
                e.getId(), u.getId(), l.getId(), l.getTitulo(),
                ex.getCodigo(), e.getDataEmprestimo(), e.getDataPrevistaDevolucao(),
                e.getDataDevolucao(), e.getRenovacoes(), e.getMulta(), e.isMultaPaga(),
                e.getStatus(), e.estaAtrasado(LocalDate.now())
        );
    }
}
