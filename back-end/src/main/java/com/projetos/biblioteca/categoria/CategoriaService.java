package com.projetos.biblioteca.categoria;

import com.projetos.biblioteca.exception.ConflitoException;
import com.projetos.biblioteca.exception.RecursoNaoEncontradoException;
import com.projetos.biblioteca.livro.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final LivroRepository livroRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findAll().stream().map(CategoriaResponseDTO::de).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        return CategoriaResponseDTO.de(categoria);
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO req) {
        if (categoriaRepository.existsByNomeIgnoreCase(req.nome())) {
            throw new ConflitoException("A categoria " + req.nome() + " já existe");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(req.nome());
        categoria.setDescricao(req.descricao());

        return CategoriaResponseDTO.de(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO req) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        if (categoriaRepository.existsByNomeIgnoreCase(req.nome())) {
            throw new ConflitoException("A categoria " + req.nome() + " já existe");
        }

        categoria.setNome(req.nome());
        categoria.setDescricao(req.descricao());
        return CategoriaResponseDTO.de(categoriaRepository.save(categoria));
    }

    @Transactional
    public void remover (Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        if (livroRepository.existsByCategoriaId(id)) {
            throw new ConflitoException("Não foi possível apagar essa categoria pois tem livros associados a ela");
        }

        categoriaRepository.delete(categoria);
    }
}
