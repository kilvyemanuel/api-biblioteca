package com.projetos.biblioteca.reserva;

import com.projetos.biblioteca.exception.ConflitoException;
import com.projetos.biblioteca.exception.RecursoNaoEncontradoException;
import com.projetos.biblioteca.exemplar.ExemplarRepository;
import com.projetos.biblioteca.exemplar.StatusExemplar;
import com.projetos.biblioteca.livro.Livro;
import com.projetos.biblioteca.livro.LivroRepository;
import com.projetos.biblioteca.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final LivroRepository livroRepository;
    private final ExemplarRepository exemplarRepository;

    @Transactional
    public ReservaResponseDTO reservar(ReservaRequestDTO req, Usuario usuario) {
        Livro livro = livroRepository.findById(req.livroId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        if (exemplarRepository.existsByLivroIdAndStatus(req.livroId(), StatusExemplar.DISPONIVEL))
            throw new ConflitoException("Reserva negada, pois existe um exemplar desse livro disponível");

        if (reservaRepository.existsByUsuarioIdAndLivroIdAndStatus(usuario.getId(), req.livroId(), StatusReserva.ATIVA))
            throw new ConflitoException("Usuário já tem uma reserva ativa para esse livro");

        Reserva reserva = new Reserva();
        reserva.setLivro(livro);
        reserva.setUsuario(usuario);

        return ReservaResponseDTO.de(reservaRepository.save(reserva));
    }

    @Transactional
    public void cancelar(Long id, Usuario usuario) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reserva não encontrada"));

        if (reserva.getUsuario().getId() != usuario.getId())
            throw new RecursoNaoEncontradoException("Reserva não encontrada");

        if (reserva.getStatus() == StatusReserva.ATENDIDA)
            throw new ConflitoException("A reserva já foi atendida");

        reserva.setStatus(StatusReserva.CANCELADA);

        reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarMinhas(Usuario usuario) {
        return reservaRepository.findByUsuarioId(usuario.getId())
                .stream().map(ReservaResponseDTO::de).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorLivro(Long livroId) {
        if (livroId != null)
            return reservaRepository.findByLivroId(livroId)
                    .stream().map(ReservaResponseDTO::de).toList();

        return reservaRepository.findAll()
                .stream().map(ReservaResponseDTO::de).toList();
    }
}
