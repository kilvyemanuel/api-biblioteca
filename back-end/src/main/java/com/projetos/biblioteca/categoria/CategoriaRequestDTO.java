package com.projetos.biblioteca.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(
        @NotBlank(message = "O nome não pode estar vazio")
        @Size(max = 80, message = "O nome precisa ter no máximo 80 caracteres")
        String nome,

        @Size(max = 255, message = "A descrição precisa ter no máximo 255 caracteres")
        String descricao
) {}
