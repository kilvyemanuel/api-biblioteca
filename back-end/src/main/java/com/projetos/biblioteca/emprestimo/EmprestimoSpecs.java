package com.projetos.biblioteca.emprestimo;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoSpecs {
    public static Specification<Emprestimo> comFiltro(EmprestimoFiltroDTO filtro) {
        return (root, query, builder) -> {
            if (filtro == null)
                return builder.conjunction();

            List<Predicate> predicates = new ArrayList<>();

            if (filtro.status() != null)
                predicates.add(builder.equal(root.get("status"), filtro.status()));

            if (filtro.usuarioId() != null)
                predicates.add(builder.equal(root.get("usuario").get("id"), filtro.usuarioId()));

            if (Boolean.TRUE.equals(filtro.atrasados())) {
                predicates.add(builder.equal(root.get("status"), StatusEmprestimo.ATIVO));
                predicates.add(builder.lessThan(root.get("dataPrevistaDevolucao"), LocalDate.now()));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
