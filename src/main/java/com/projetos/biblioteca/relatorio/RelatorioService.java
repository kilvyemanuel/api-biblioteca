package com.projetos.biblioteca.relatorio;

import com.projetos.biblioteca.config.BibliotecaProperties;
import com.projetos.biblioteca.emprestimo.EmprestimoRepository;
import com.projetos.biblioteca.emprestimo.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {
    private final EmprestimoRepository emprestimoRepository;
    private final EmprestimoService emprestimoService;
    private final BibliotecaProperties properties;

    List<TopLivroResponseDTO> listarMaisEmprestado(Long limite) {
        return emprestimoRepository.findLivrosMaisEmprestados(PageRequest.of(0, limite.intValue()));
    }

    @Transactional(readOnly = true)
    List<EmprestimoAtrasadoResponseDTO> listarAtrasados() {
        return emprestimoService.listarAtrasados()
                .stream().map(e -> EmprestimoAtrasadoResponseDTO.de(e, properties.multaPorDia())).toList();
    }
}
