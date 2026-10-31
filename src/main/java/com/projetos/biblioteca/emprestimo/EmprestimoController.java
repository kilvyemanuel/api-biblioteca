package com.projetos.biblioteca.emprestimo;

import com.projetos.biblioteca.usuario.Usuario;
import com.projetos.biblioteca.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {
    private final EmprestimoService emprestimoService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> emprestar(@Valid @RequestBody EmprestimoRequestDTO req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.emprestar(req));
    }

    @GetMapping
    public Page<EmprestimoResponseDTO> listar(EmprestimoFiltroDTO filtro,
                                              Pageable pageable) {
        return emprestimoService.listar(filtro, pageable);
    }

    @GetMapping("/meus")
    public Page<EmprestimoResponseDTO> meus(@RequestParam(required = false) StatusEmprestimo status,
                                            Pageable pageable,
                                            Authentication auth) {
        return emprestimoService.listarDoUsuario(usuarioLogado(auth).getId(), status, pageable);
    }

    @GetMapping("/{id}")
    public EmprestimoResponseDTO buscar(@PathVariable Long id,
                                        Authentication auth) {
        return emprestimoService.buscar(id, usuarioLogado(auth));
    }

    @PostMapping("/{id}/devolucao")
    public EmprestimoResponseDTO devolver(@PathVariable Long id) {
        return emprestimoService.devolver(id);
    }

    @PostMapping("/{id}/renovacao")
    public EmprestimoResponseDTO renovar(@PathVariable Long id,
                                         Authentication auth) {
        return emprestimoService.renovar(id, usuarioLogado(auth));
    }

    @PostMapping("/{id}/pagamento-multa")
    public EmprestimoResponseDTO pagarMulta(@PathVariable Long id) {
        return emprestimoService.pagarMulta(id);
    }

    private Usuario usuarioLogado(Authentication auth) {
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
