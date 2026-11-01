package com.projetos.biblioteca.emprestimo;

import com.projetos.biblioteca.relatorio.TopLivroResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>, JpaSpecificationExecutor<Emprestimo> {
    long countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
    boolean existsByUsuarioIdAndMultaPagaFalseAndMultaGreaterThan(Long usuarioId, BigDecimal valor);
    boolean existsByExemplarId(Long exemplarId);
    Page<Emprestimo> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status, Pageable pageable);

    @Query( "SELECT new com.projetos.biblioteca.relatorio.TopLivroResponseDTO(l.id, l.titulo, COUNT(e)) " +
            "FROM Emprestimo e " +
            "JOIN e.exemplar ex " +
            "JOIN ex.livro l " +
            "GROUP BY l.id, l.titulo " +
            "ORDER BY COUNT(e) DESC"
    )
    List<TopLivroResponseDTO> findLivrosMaisEmprestados(Pageable pageable);
}
