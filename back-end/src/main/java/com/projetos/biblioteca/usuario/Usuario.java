package com.projetos.biblioteca.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false)
    private String nome;

    @Email @Column(length = 180, nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", length = 100, nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Papel papel = Papel.MEMBRO;

    @Column(nullable = false)
    private boolean ativo = true;

    @CreationTimestamp @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;
}
