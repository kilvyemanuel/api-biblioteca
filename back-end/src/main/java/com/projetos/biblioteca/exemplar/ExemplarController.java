package com.projetos.biblioteca.exemplar;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExemplarController {
    private final ExemplarService exemplarService;

    @GetMapping("/livros/{livroId}/exemplares")
    public List<ExemplarResponseDTO> listar(@PathVariable Long livroId) {
        return exemplarService.listar(livroId);
    }

    @PostMapping("/livros/{livroId}/exemplares")
    public ResponseEntity<ExemplarResponseDTO> criar(@PathVariable Long livroId,
                                                     @Valid @RequestBody ExemplarRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(exemplarService.criar(livroId, req));
    }

    @PatchMapping("/exemplares/{id}/status")
    public ExemplarResponseDTO alterarStatus(@PathVariable Long id,
                                             @Valid @RequestBody AlterarStatusExemplarRequestDTO req) {
        return exemplarService.alterarStatus(id, req.status());
    }

    @DeleteMapping("/exemplares/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        exemplarService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
