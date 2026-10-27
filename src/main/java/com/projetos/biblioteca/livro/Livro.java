package com.projetos.biblioteca.livro;

import com.projetos.biblioteca.autor.Autor;
import com.projetos.biblioteca.categoria.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "livro")
@Getter @Setter
@NoArgsConstructor
public class Livro {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String titulo;

    @Column(length = 17, nullable = false, unique = true)
    private String isbn;

    @Min(value = 1, message = "O ano de publicação deve ser maior que 0")
    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToMany
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "livro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();

    @CreationTimestamp @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}
