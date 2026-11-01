package com.projetos.biblioteca.relatorio;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {
    private final RelatorioService relatorioService;

    @GetMapping("/livros-mais-emprestados")
    List<TopLivroResponseDTO> listarMaisEmprestados(@RequestParam Long limite) {
        return relatorioService.listarMaisEmprestado(limite);
    }
}
