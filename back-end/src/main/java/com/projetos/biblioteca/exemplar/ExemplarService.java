package com.projetos.biblioteca.exemplar;

import com.projetos.biblioteca.emprestimo.EmprestimoRepository;
import com.projetos.biblioteca.exception.ConflitoException;
import com.projetos.biblioteca.exception.RecursoNaoEncontradoException;
import com.projetos.biblioteca.livro.Livro;
import com.projetos.biblioteca.livro.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExemplarService {
    private final ExemplarRepository exemplarRepository;
    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    @Transactional(readOnly = true)
    public List<ExemplarResponseDTO> listar(Long livroId) {
        if (!livroRepository.existsById(livroId))
            throw new RecursoNaoEncontradoException("Livro não encontrado");

        return exemplarRepository.findByLivroId(livroId).stream().map(ExemplarResponseDTO::de).toList();
    }

    @Transactional
    public ExemplarResponseDTO criar(Long livroId, ExemplarRequestDTO req) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        if (exemplarRepository.existsByCodigo(req.codigo()))
            throw new ConflitoException("Exemplar com o código " + req.codigo() + " já existe");

        Exemplar exemplar = new Exemplar();
        exemplar.setLivro(livro);
        exemplar.setCodigo(req.codigo());
        return ExemplarResponseDTO.de(exemplarRepository.save(exemplar));
    }

    @Transactional
    public ExemplarResponseDTO alterarStatus(Long id, StatusExemplar status) {
        Exemplar exemplar = exemplarRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar não encontrado"));

        if (exemplar.getStatus() == StatusExemplar.EMPRESTADO)
            throw new ConflitoException("Não é possivel alterar o status de um exemplar EMPRESTADO manualmente");

        if (status == StatusExemplar.EMPRESTADO)
            throw new ConflitoException("Não é possivel alterar o status para EMPRESTADO manualmente");

        exemplar.setStatus(status);
        return ExemplarResponseDTO.de(exemplarRepository.save(exemplar));
    }

    @Transactional
    public void remover(Long id) {
        Exemplar exemplar = exemplarRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exemplar não encontrado"));

        if (emprestimoRepository.existsByExemplarId(id))
            throw new ConflitoException("Um exemplar com empréstimo cadastrado não pode ser excluído");

        exemplarRepository.delete(exemplar);
    }
}
