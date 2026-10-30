package com.projetos.biblioteca.usuario;

import com.projetos.biblioteca.auth.RegistroRequestDTO;
import com.projetos.biblioteca.exception.ConflitoException;
import com.projetos.biblioteca.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioAdminResponseDTO> listar(Papel papel, Boolean ativo, Pageable pageable) {
        if (papel != null && ativo != null) {
            return usuarioRepository.findByPapelAndAtivo(papel, ativo, pageable).map(UsuarioAdminResponseDTO::de);
        } else if (papel != null) {
            return usuarioRepository.findByPapel(papel, pageable).map(UsuarioAdminResponseDTO::de);
        } else if (ativo != null) {
            return usuarioRepository.findByAtivo(ativo, pageable).map(UsuarioAdminResponseDTO::de);
        } else {
            return usuarioRepository.findAll(pageable).map(UsuarioAdminResponseDTO::de);
        }
    }

    @Transactional
    public UsuarioAdminResponseDTO alterarPapel(Long id, Papel papel, String emailAdmin) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (usuario.getEmail().equals(emailAdmin)) {
            throw new ConflitoException("Não é possível alterar seu próprio papel");
        }

        usuario.setPapel(papel);

        return UsuarioAdminResponseDTO.de(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioAdminResponseDTO alterarAtivo(Long id, boolean ativo, String emailAdmin) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (usuario.getEmail().equals(emailAdmin)) {
            throw new ConflitoException("Não é possível alterar seu próprio status");
        }

        usuario.setAtivo(ativo);

        return UsuarioAdminResponseDTO.de(usuarioRepository.save(usuario));
    }
}
