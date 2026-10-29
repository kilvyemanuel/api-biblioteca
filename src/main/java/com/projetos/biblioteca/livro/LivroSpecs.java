package com.projetos.biblioteca.livro;

import com.projetos.biblioteca.exemplar.Exemplar;
import com.projetos.biblioteca.exemplar.StatusExemplar;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class LivroSpecs {
    public static Specification<Livro> comFiltro(LivroFiltroDTO filtro) {
        return (root, query, cb) -> {
            if (filtro == null) {
                return cb.conjunction();
            }

            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(filtro.titulo())) {
                String texto = filtro.titulo().trim().toLowerCase(java.util.Locale.ROOT)
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
                predicates.add(cb.like(cb.lower(root.get("titulo")), "%" + texto + "%", '\\'));
            }

            if (filtro.categoriaId() != null) {
                predicates.add(cb.equal(root.get("categoria").get("id"), filtro.categoriaId()));
            }

            if (filtro.autorId() != null) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Livro> livroSub = sub.from(Livro.class);
                var autores = livroSub.join("autores");
                sub.select(livroSub.get("id"))
                        .where(
                                cb.equal(livroSub.get("id"), root.get("id")),
                                cb.equal(autores.get("id"), filtro.autorId()));
                predicates.add(cb.exists(sub));
            }

            if (filtro.disponivel() != null) {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Exemplar> exemplar = sub.from(Exemplar.class);
                sub.select(exemplar.get("id"))
                        .where(
                                cb.equal(exemplar.get("livro").get("id"), root.get("id")),
                                cb.equal(exemplar.get("status"), StatusExemplar.DISPONIVEL));
                Predicate temDisponivel = cb.exists(sub);
                predicates.add(filtro.disponivel() ? temDisponivel : cb.not(temDisponivel));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
