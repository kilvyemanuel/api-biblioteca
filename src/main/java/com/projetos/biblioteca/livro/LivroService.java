package com.projetos.biblioteca.livro;

import com.projetos.biblioteca.autor.Autor;
import com.projetos.biblioteca.autor.AutorRepository;
import com.projetos.biblioteca.categoria.Categoria;
import com.projetos.biblioteca.categoria.CategoriaRepository;
import com.projetos.biblioteca.exception.ConflitoException;
import com.projetos.biblioteca.exception.RecursoNaoEncontradoException;
import com.projetos.biblioteca.exemplar.ExemplarRepository;
import com.projetos.biblioteca.exemplar.StatusExemplar;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LivroService {
    private final LivroRepository livroRepository;
    private final CategoriaRepository categoriaRepository;
    private final AutorRepository autorRepository;
    private final ExemplarRepository exemplarRepository;

    @Transactional(readOnly = true)
    public Page<LivroResumoResponseDTO> listar(LivroFiltroDTO filtro, Pageable pageable) {
        return livroRepository.findAll(LivroSpecs.comFiltro(filtro), pageable)
                .map(livro -> LivroResumoResponseDTO.de
                        (livro, exemplarRepository.countByLivroIdAndStatus(livro.getId(), StatusExemplar.DISPONIVEL)));
    }

    @Transactional(readOnly = true)
    public LivroDetalheResponseDTO buscar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        return LivroDetalheResponseDTO.de(livro);
    }

    @Transactional
    public LivroDetalheResponseDTO criar(LivroRequestDTO req) {
        if (livroRepository.existsByIsbn(req.isbn()))
            throw new ConflitoException("O isbn " + req.isbn() + " já existe");

        Categoria categoria = categoriaRepository.findById(req.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        Set<Autor> autores = new HashSet<>();
        for (Long autorId : req.autoresIds()) {
            autorRepository.findById(autorId)
                    .ifPresent(autores::add);
        }

        if (autores.isEmpty())
            throw new RecursoNaoEncontradoException("Autores não encontrados");

        Livro livro = new Livro();
        livro.setTitulo(req.titulo());
        livro.setIsbn(req.isbn());
        livro.setAnoPublicacao(req.anoPublicacao());
        livro.setSinopse(req.sinopse());
        livro.setCategoria(categoria);
        livro.setAutores(autores);

        return LivroDetalheResponseDTO.de(livroRepository.save(livro));
    }

    @Transactional
    public LivroDetalheResponseDTO atualizar(Long id, LivroRequestDTO req) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        Categoria categoria = categoriaRepository.findById(req.categoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        Set<Autor> autores = new HashSet<>();
        for (Long autorId : req.autoresIds()) {
            autorRepository.findById(autorId)
                    .ifPresent(autores::add);
        }

        if (autores.isEmpty())
            throw new RecursoNaoEncontradoException("Autores não encontrados");

        livro.setTitulo(req.titulo());
        livro.setIsbn(req.isbn());
        livro.setAnoPublicacao(req.anoPublicacao());
        livro.setSinopse(req.sinopse());
        livro.setCategoria(categoria);
        livro.setAutores(autores);

        return LivroDetalheResponseDTO.de(livroRepository.save(livro));
    }

    @Transactional
    public void remover(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        if (exemplarRepository.existsByLivroId(id))
            throw new ConflitoException("Não foi possível apagar esse livro pois tem exemplares associados a ele");

        livroRepository.delete(livro);
    }
}
