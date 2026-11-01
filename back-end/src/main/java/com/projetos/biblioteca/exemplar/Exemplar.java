package com.projetos.biblioteca.exemplar;

import com.projetos.biblioteca.livro.Livro;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "exemplar")
@Getter @Setter
@NoArgsConstructor
public class Exemplar {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @Column(length = 30, nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusExemplar status = StatusExemplar.DISPONIVEL;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;
}
