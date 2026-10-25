package com.projetos.biblioteca.auth;

import com.projetos.biblioteca.usuario.UsuarioResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO req) {
        return ResponseEntity.ok().body(authService.login(req));
    }
}
