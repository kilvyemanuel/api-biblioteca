package com.projetos.biblioteca.usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public UsuarioResponseDTO meuPerfil(Authentication auth) {
        return UsuarioResponseDTO.de(usuarioService.buscarPorEmail(auth.getName()));
    }
}
