package com.projetos.biblioteca.auth;

import com.projetos.biblioteca.usuario.Usuario;
import com.projetos.biblioteca.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, DisabledException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!usuario.isAtivo()) throw new DisabledException("Usuário inativo");

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .roles(String.valueOf(usuario.getPapel()))
                .build();
    }
}
