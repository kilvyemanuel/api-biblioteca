package com.projetos.biblioteca.usuario;

import jakarta.validation.constraints.NotNull;

public record AlterarPapelRequestDTO(
        @NotNull(message = "O papel é obrigatório")
        Papel papel
) {}
