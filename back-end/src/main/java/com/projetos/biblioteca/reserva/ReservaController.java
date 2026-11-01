package com.projetos.biblioteca.reserva;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.projetos.biblioteca.usuario.Usuario;
import com.projetos.biblioteca.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> reservar(@Valid @RequestBody ReservaRequestDTO req,
                                                       Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.reservar(req, usuarioLogado(auth)));
    }

    @GetMapping("/minhas")
    public List<ReservaResponseDTO> minhas(Authentication auth) {
        return reservaService.listarMinhas(usuarioLogado(auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
                                         Authentication auth) {
        reservaService.cancelar(id, usuarioLogado(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<ReservaResponseDTO> listarPorLivro(@RequestParam(required = false) Long livroId) {
        return reservaService.listarPorLivro(livroId);
    }

    private Usuario usuarioLogado(Authentication auth) {
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
