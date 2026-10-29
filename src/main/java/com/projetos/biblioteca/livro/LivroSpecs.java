package com.projetos.biblioteca.livro;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class LivroSpecs {
    public static Specification<Livro> comFiltro(LivroFiltroDTO filtro) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (filtro == null) {
                return cb.conjunction();
            }

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
                query.distinct(true);
                var autorJoin = root.join("autores");
                predicates.add(cb.equal(autorJoin.get("id"), filtro.autorId()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
