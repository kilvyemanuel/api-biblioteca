package com.projetos.biblioteca.auth;

import com.projetos.biblioteca.usuario.UsuarioResponseDTO;
import com.projetos.biblioteca.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public UsuarioResponseDTO registrar(RegistroRequestDTO req) {
        return usuarioService.cadastrar(req);
    }

    @Transactional
    public TokenResponseDTO login(LoginRequestDTO req) {
        String email = req.email().trim().toLowerCase();

        var authToken = new UsernamePasswordAuthenticationToken(email, req.senha());
        authenticationManager.authenticate(authToken);

        String token = jwtService.gerarToken(email);
        Instant expiracao = jwtService.extrairExpiracao(token);
        return new TokenResponseDTO(token,"Bearer", expiracao);
    }
}
