package com.projetos.biblioteca.emprestimo;

public record EmprestimoFiltroDTO(
        StatusEmprestimo status,
        Long usuarioId,
        Boolean atrasados
) {}
