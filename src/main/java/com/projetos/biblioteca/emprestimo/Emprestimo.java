package com.projetos.biblioteca.emprestimo;

import com.projetos.biblioteca.exemplar.Exemplar;
import com.projetos.biblioteca.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "emprestimo")
@Getter @Setter
@NoArgsConstructor
public class Emprestimo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exemplar_id", nullable = false)
    private Exemplar exemplar;

    @CreationTimestamp
    @Column(name = "data_emprestimo", nullable = false, updatable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @Column(nullable = false)
    private Short renovacoes = 0;

    @Min(value = 0)
    @Column(nullable = false)
    private BigDecimal multa = BigDecimal.valueOf(0);

    @Column(name = "multa_paga", nullable = false)
    private boolean multaPaga = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusEmprestimo status = StatusEmprestimo.ATIVO;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    public boolean estaAtrasado(LocalDate hoje) {
        if (status != StatusEmprestimo.ATIVO) return false;
        return hoje.isAfter(dataPrevistaDevolucao);
    }

    public long diasDeAtraso(LocalDate hoje) {
        LocalDate ref = dataDevolucao != null ? dataDevolucao : hoje;
        if (ref.isAfter(dataPrevistaDevolucao)) return ChronoUnit.DAYS.between(dataPrevistaDevolucao, ref);
        return 0;
    }
}
