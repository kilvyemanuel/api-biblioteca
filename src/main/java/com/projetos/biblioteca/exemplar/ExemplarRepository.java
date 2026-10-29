package com.projetos.biblioteca.exemplar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    List<Exemplar> findByLivroId(Long livroId);
    long countByLivroId(Long livroId);
    long countByLivroIdAndStatus(Long livroId, StatusExemplar status);
    boolean existsByLivroId(Long livroId);
    boolean existsByCodigo(String codigo);
}
