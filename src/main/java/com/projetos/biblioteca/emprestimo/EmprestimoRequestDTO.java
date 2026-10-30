package com.projetos.biblioteca.emprestimo;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequestDTO(
        @NotNull(message = "O ID do usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "O ID do livro é obrigatório")
        Long livroId
) {}
