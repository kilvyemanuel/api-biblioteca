package com.projetos.biblioteca.exemplar;

import jakarta.validation.constraints.NotNull;

public record AlterarStatusExemplarRequestDTO(
        @NotNull(message = "O status é obrigatório")
        StatusExemplar status
) {}
