package com.projetos.biblioteca.livro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LivroRepository extends JpaRepository<Livro, Long>, JpaSpecificationExecutor<Livro> {
    boolean existsByIsbn(String isbn);
    boolean existsByCategoriaId(Long categoriaId);
    boolean existsByAutoresId(Long autorId);
}
