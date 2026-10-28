package com.projetos.biblioteca.autor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/autores")
@RequiredArgsConstructor
public class AutorController {
    private final AutorService autorService;

    @GetMapping
    public Page<AutorResponseDTO> listar(@RequestParam(required = false) String nome,
                                         @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return autorService.listar(nome, pageable);
    }

    @GetMapping("/{id}")
    public AutorResponseDTO buscar(@PathVariable Long id) {
        return autorService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<AutorResponseDTO> criar(@Valid @RequestBody AutorRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.criar(req));
    }

    @PutMapping("/{id}")
    public AutorResponseDTO atualizar(@PathVariable Long id,
                                      @Valid @RequestBody AutorRequestDTO req) {
        return autorService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        autorService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
