package com.projetos.biblioteca.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Usuario> findByPapelAndAtivo(Papel papel, Boolean ativo, Pageable pageable);
    Page<Usuario> findByPapel(Papel papel, Pageable pageable);
    Page<Usuario> findByAtivo(Boolean ativo, Pageable pageable);
}
