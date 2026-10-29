package com.projetos.biblioteca.exemplar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExemplarRequestDTO(
        @NotBlank(message = "O código do exemplar não pode ser vazio")
        @Size(max = 30, message = "O código do exemplar tem no máximo 30 caracteres")
        String codigo
) {}
