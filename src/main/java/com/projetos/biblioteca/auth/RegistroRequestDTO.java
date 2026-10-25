package com.projetos.biblioteca.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequestDTO(
        @NotBlank(message = "O nome não pode estar vazio")
        @Size(max = 120, message = "O nome precisa ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "O email não pode estar vazio")
        @Email(message = "O email precisa ser válido")
        String email,

        @NotBlank(message = "A senha não pode estar vazia")
        @Size(min = 8, max = 100, message = "A senha precisa ter entre 8 e 100 caracteres")
        String senha
) {}
