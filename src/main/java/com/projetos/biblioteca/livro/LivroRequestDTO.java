package com.projetos.biblioteca.livro;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record LivroRequestDTO(
        @NotBlank(message = "O título não pode ficar em branco")
        @Size(max = 200, message = "O tamanho máximo do título é 200 caracteres")
        String titulo,

        @NotBlank(message = "O isbn não pode ficar em branco")
        @Size(max = 17, message = "O tamanho máximo do isbn é 17 caracteres")
        String isbn,

        @Min(value = 1, message = "O ano de publicação deve ser maior que 0")
        Short anoPublicacao,

        String sinopse,

        @NotBlank(message = "O livro precisa ter uma categoria")
        Long categoriaId,

        @NotEmpty(message = "O livro precisa ter pelo menos um autor")
        Set<Long> autoresIds
) {}
