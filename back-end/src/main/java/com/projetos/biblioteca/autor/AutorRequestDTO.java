package com.projetos.biblioteca.autor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AutorRequestDTO(
        @NotBlank(message = "O nome não pode estar vazio")
        @Size(max = 150, message = "O nome precisa ter no máximo 150 caracteres")
        String nome,

        @Size(max = 80, message = "A nacionalidade precisa ter no máximo 80 caracteres")
        String nacionalidade
) {}
