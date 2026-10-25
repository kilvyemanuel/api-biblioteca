package com.projetos.biblioteca.usuario;

import com.projetos.biblioteca.auth.RegistroRequestDTO;
import com.projetos.biblioteca.exception.ConflitoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO cadastrar(RegistroRequestDTO req) {
        String email = req.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("O email informado já existe");
        }

        String senha = passwordEncoder.encode(req.senha());

        Usuario usuario = new Usuario();
        usuario.setNome(req.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(senha);

        usuarioRepository.save(usuario);
        return UsuarioResponseDTO.de(usuario);
    }
}
