package com.projetos.biblioteca.relatorio;

import com.projetos.biblioteca.emprestimo.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {
    private final EmprestimoRepository emprestimoRepository;

    List<TopLivroResponseDTO> listarMaisEmprestado(Long limite) {
        return emprestimoRepository.findLivrosMaisEmprestados(PageRequest.of(0, limite.intValue()));
    }
}
