package com.projetos.biblioteca.emprestimo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    long countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
    boolean existsByUsuarioIdAndMultaPagaFalseAndMultaGreaterThan(Long usuarioId, BigDecimal valor);
    Page<Emprestimo> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status, Pageable pageable);
}
