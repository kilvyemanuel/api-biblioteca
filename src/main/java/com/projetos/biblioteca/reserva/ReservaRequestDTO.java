package com.projetos.biblioteca.reserva;

import jakarta.validation.constraints.NotNull;

public record ReservaRequestDTO(
        @NotNull(message = "O ID do livro é obrigatório")
        Long livroId
) {}
