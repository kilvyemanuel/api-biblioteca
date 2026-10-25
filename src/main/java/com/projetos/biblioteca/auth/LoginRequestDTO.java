package com.projetos.biblioteca.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O email não pode estar vazio")
        @Email(message = "O email precisa ser válido")
        String email,

        @NotBlank(message = "A senha não pode estar vazia")
        String senha
) {}
