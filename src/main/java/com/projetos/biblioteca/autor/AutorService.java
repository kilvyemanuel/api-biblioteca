package com.projetos.biblioteca.autor;

import com.projetos.biblioteca.exception.ConflitoException;
import com.projetos.biblioteca.exception.RecursoNaoEncontradoException;
import com.projetos.biblioteca.livro.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutorService {
    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    @Transactional(readOnly = true)
    public Page<AutorResponseDTO> listar(String nome, Pageable pageable) {
        if (nome != null) {
            return autorRepository.findByNomeContainingIgnoreCase(nome, pageable).map(AutorResponseDTO::de);
        } else {
            return autorRepository.findAll(pageable).map(AutorResponseDTO::de);
        }
    }

    @Transactional(readOnly = true)
    public AutorResponseDTO buscar(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor não encontrado"));

        return AutorResponseDTO.de(autor);
    }

    @Transactional
    public AutorResponseDTO criar(AutorRequestDTO req) {
        Autor autor = new Autor();
        autor.setNome(req.nome().trim());
        autor.setNacionalidade(req.nacionalidade());

        return AutorResponseDTO.de(autorRepository.save(autor));
    }

    @Transactional
    public AutorResponseDTO atualizar(Long id, AutorRequestDTO req) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor não encontrado"));

        autor.setNome(req.nome().trim());
        autor.setNacionalidade(req.nacionalidade());

        return AutorResponseDTO.de(autorRepository.save(autor));
    }

    @Transactional
    public void remover(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Autor não encontrado"));

        if (livroRepository.existsByAutoresId(id)) {
            throw new ConflitoException("Não foi possível apagar esse autor pois tem livros associados a ele");
        }

        autorRepository.delete(autor);
    }
}
