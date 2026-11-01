package com.projetos.biblioteca.exemplar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    List<Exemplar> findByLivroId(Long livroId);
    Optional<Exemplar> findFirstByLivroIdAndStatus(Long livroId, StatusExemplar status);
    long countByLivroId(Long livroId);
    long countByLivroIdAndStatus(Long livroId, StatusExemplar status);
    boolean existsByLivroId(Long livroId);
    boolean existsByLivroIdAndStatus(Long livroId, StatusExemplar status);
    boolean existsByCodigo(String codigo);
}
