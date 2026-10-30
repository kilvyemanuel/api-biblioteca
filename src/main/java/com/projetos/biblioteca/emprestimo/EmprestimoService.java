package com.projetos.biblioteca.emprestimo;

import com.projetos.biblioteca.config.BibliotecaProperties;
import com.projetos.biblioteca.exception.ConflitoException;
import com.projetos.biblioteca.exception.RecursoNaoEncontradoException;
import com.projetos.biblioteca.exception.RegraDeNegocioException;
import com.projetos.biblioteca.exemplar.Exemplar;
import com.projetos.biblioteca.exemplar.ExemplarRepository;
import com.projetos.biblioteca.exemplar.StatusExemplar;
import com.projetos.biblioteca.usuario.Papel;
import com.projetos.biblioteca.usuario.Usuario;
import com.projetos.biblioteca.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioService usuarioService;
    private final ExemplarRepository exemplarRepository;
    private final BibliotecaProperties properties;

    @Transactional
    public EmprestimoResponseDTO emprestar(EmprestimoRequestDTO req) {
        Usuario usuario = usuarioService.buscarPorId(req.usuarioId());
        if (!usuario.isAtivo())
            throw new ConflitoException("Usuário informado não está ativo");

        if (emprestimoRepository.countByUsuarioIdAndStatus(req.usuarioId(), StatusEmprestimo.ATIVO) == properties.limiteEmprestimosAtivos())
            throw new RegraDeNegocioException("Um usuário não pode ter mais de 3 empréstimos ativos");

        if (emprestimoRepository.existsByUsuarioIdAndMultaPagaFalseAndMultaGreaterThan(req.usuarioId(), BigDecimal.ZERO))
            throw new RegraDeNegocioException("O usuário informado tem uma multa pendente");

        Exemplar exemplar = exemplarRepository.findFirstByLivroIdAndStatus(req.livroId(), StatusExemplar.DISPONIVEL)
                .orElseThrow(() -> new ConflitoException("Não existe um exemplar disponível para esse livro"));

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setExemplar(exemplar);
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(properties.prazoDias()));

        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        return EmprestimoResponseDTO.de(emprestimoRepository.save(emprestimo));
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponseDTO> listarDoUsuario(Long usuarioId, StatusEmprestimo status, Pageable pageable) {
        if (status != null)
            return emprestimoRepository.findByUsuarioIdAndStatus(usuarioId, status, pageable)
                    .map(EmprestimoResponseDTO::de);

        return emprestimoRepository.findByUsuarioId(usuarioId, pageable)
                .map(EmprestimoResponseDTO::de);
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO buscar(Long id, Usuario solicitante) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Emprestimo não encontrado"));

        if (solicitante.getPapel() == Papel.MEMBRO && emprestimo.getUsuario().getId() != solicitante.getId())
            throw new RecursoNaoEncontradoException("Emprestimo não encontrado");

        return EmprestimoResponseDTO.de(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO devolver(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Emprestimo não encontrado"));

        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO)
            throw new RegraDeNegocioException("O emprestimo não está ativo");

        emprestimo.getExemplar().setStatus(StatusExemplar.DISPONIVEL);
        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        if (emprestimo.estaAtrasado(LocalDate.now())) {
            emprestimo.setMulta(properties.multaPorDia().multiply(BigDecimal.valueOf(emprestimo.diasDeAtraso(LocalDate.now()))));
        }

        return EmprestimoResponseDTO.de(emprestimoRepository.save(emprestimo));
    }
}
