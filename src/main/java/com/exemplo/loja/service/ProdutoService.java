package com.exemplo.loja.service;

import com.exemplo.loja.model.Categoria;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.model.ProdutoImagem;
import com.exemplo.loja.repository.CategoriaRepository;
import com.exemplo.loja.repository.ProdutoImagemRepository;
import com.exemplo.loja.repository.ProdutoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Regras de negocio do CRUD de produtos, da consulta com filtros (R4) e do
 * gerenciamento das imagens dos produtos (armazenadas no banco como bytes).
 */
@Service
public class ProdutoService {

    /** Limite de imagens por produto (R3). */
    public static final int MAX_IMAGENS = 10;

    private final ProdutoRepository produtoRepo;
    private final CategoriaRepository categoriaRepo;
    private final ProdutoImagemRepository imagemRepo;

    public ProdutoService(ProdutoRepository produtoRepo, CategoriaRepository categoriaRepo,
                          ProdutoImagemRepository imagemRepo) {
        this.produtoRepo = produtoRepo;
        this.categoriaRepo = categoriaRepo;
        this.imagemRepo = imagemRepo;
    }

    public List<Produto> listar() {
        return produtoRepo.findAll();
    }

    public Produto buscar(Long id) {
        return produtoRepo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Produto nao encontrado: " + id));
    }

    /** Consulta com filtros opcionais combinados (nome, categoria, faixa de preco). */
    public List<Produto> filtrar(String nome, Long categoriaId,
                                 BigDecimal precoMin, BigDecimal precoMax) {
        return produtoRepo.filtrar(
                (nome == null || nome.isBlank()) ? null : nome,
                categoriaId,
                precoMin,
                precoMax);
    }

    private Categoria resolverCategoria(Long categoriaId) {
        return categoriaRepo.findById(categoriaId).orElseThrow(
                () -> new IllegalArgumentException("Categoria inexistente: " + categoriaId));
    }

    @Transactional
    public Produto salvar(Produto produto, Long categoriaId, MultipartFile[] imagens) {
        produto.setCategoria(resolverCategoria(categoriaId));
        Produto salvo = produtoRepo.save(produto);
        anexarImagens(salvo, imagens);
        return salvo;
    }

    @Transactional
    public Produto atualizar(Long id, Produto dados, Long categoriaId, MultipartFile[] imagens) {
        Produto existente = buscar(id);
        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        existente.setPreco(dados.getPreco());
        existente.setEstoque(dados.getEstoque());
        existente.setCategoria(resolverCategoria(categoriaId));
        Produto salvo = produtoRepo.save(existente);
        anexarImagens(salvo, imagens);
        return salvo;
    }

    @Transactional
    public void excluir(Long id) {
        produtoRepo.deleteById(id);
    }

    // ---------------------- Imagens ----------------------

    public long contarImagens(Long produtoId) {
        return imagemRepo.countByProdutoId(produtoId);
    }

    public List<Long> listarImagemIds(Long produtoId) {
        return imagemRepo.findIdsByProdutoId(produtoId);
    }

    public ProdutoImagem buscarImagem(Long imagemId) {
        return imagemRepo.findById(imagemId).orElseThrow(
                () -> new IllegalArgumentException("Imagem nao encontrada: " + imagemId));
    }

    @Transactional
    public void excluirImagem(Long imagemId) {
        imagemRepo.deleteById(imagemId);
    }

    /** Persiste cada arquivo de imagem (nao vazio) como bytes vinculados ao produto. */
    private void anexarImagens(Produto produto, MultipartFile[] imagens) {
        if (imagens == null) {
            return;
        }
        for (MultipartFile arquivo : imagens) {
            if (arquivo == null || arquivo.isEmpty()) {
                continue;
            }
            try {
                imagemRepo.save(new ProdutoImagem(
                        arquivo.getBytes(),
                        arquivo.getContentType(),
                        arquivo.getOriginalFilename(),
                        produto));
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Falha ao ler a imagem: " + arquivo.getOriginalFilename(), e);
            }
        }
    }
}
