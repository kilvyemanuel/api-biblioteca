package com.projetos.biblioteca.livro;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroController {
    private final LivroService livroService;

    @GetMapping
    public Page<LivroResumoResponseDTO> listar(LivroFiltroDTO filtro,
                                               Pageable pageable) {
        return livroService.listar(filtro, pageable);
    }

    @GetMapping("/{id}")
    public LivroDetalheResponseDTO buscar(@PathVariable Long id) {
        return livroService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<LivroDetalheResponseDTO> criar(@Valid @RequestBody LivroRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.criar(req));
    }

    @PutMapping("/{id}")
    public LivroDetalheResponseDTO atualizar(@PathVariable Long id,
                                             @Valid @RequestBody LivroRequestDTO req) {
        return livroService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover (@PathVariable Long id) {
        livroService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
