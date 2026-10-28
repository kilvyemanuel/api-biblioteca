package com.projetos.biblioteca.livro;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    boolean existsByCategoriaId(Long categoriaId);
    boolean existsByAutoresId(Long autorId);
}
