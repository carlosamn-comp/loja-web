package com.exemplo.loja.service;

import com.exemplo.loja.model.Categoria;
import com.exemplo.loja.repository.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negocio do CRUD de categorias.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepo;

    public CategoriaService(CategoriaRepository categoriaRepo) {
        this.categoriaRepo = categoriaRepo;
    }

    public List<Categoria> listar() {
        return categoriaRepo.findAll();
    }

    public Categoria buscar(Long id) {
        return categoriaRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Categoria nao encontrada: " + id));
    }

    public boolean nomeDisponivel(String nome) {
        return !categoriaRepo.existsByNome(nome);
    }

    public boolean nomeDisponivelParaEdicao(String nome, Long id) {
        return categoriaRepo.findByNome(nome)
                .map(c -> c.getId().equals(id))
                .orElse(true);
    }

    @Transactional
    public Categoria salvar(Categoria categoria) {
        return categoriaRepo.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, Categoria dados) {
        Categoria existente = buscar(id);
        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        return categoriaRepo.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        categoriaRepo.deleteById(id);
    }
}
