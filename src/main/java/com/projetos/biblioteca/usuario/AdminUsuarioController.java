package com.projetos.biblioteca.usuario;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public Page<UsuarioAdminResponseDTO> listar(@RequestParam(required = false) Papel papel,
                                                @RequestParam(required = false) Boolean ativo,
                                                @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return usuarioService.listar(papel, ativo, pageable);
    }

    @PatchMapping("/usuarios/{id}/papel")
    public UsuarioAdminResponseDTO alterarPapel(@PathVariable Long id,
                                                @Valid @RequestBody AlterarPapelRequestDTO req,
                                                Authentication auth) {
        return usuarioService.alterarPapel(id, req.papel(), auth.getName());
    }

    @PatchMapping("/usuarios/{id}/ativo")
    public UsuarioAdminResponseDTO alterarAtivo(@PathVariable Long id,
                                                @Valid @RequestBody AlterarAtivoRequestDTO req,
                                                Authentication auth) {
        return usuarioService.alterarAtivo(id, req.ativo(), auth.getName());
    }
}
