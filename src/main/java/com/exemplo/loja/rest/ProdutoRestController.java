package com.exemplo.loja.rest;

import com.exemplo.loja.model.Categoria;
import com.exemplo.loja.model.Loja;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.repository.CategoriaRepository;
import com.exemplo.loja.repository.LojaRepository;
import com.exemplo.loja.repository.ProdutoRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST-API de produtos. No marketplace, cada produto pertence a uma loja e a uma
 * categoria (ambas informadas no corpo via id: {"loja":{"id":1},"categoria":{"id":1}}).
 */
@RestController
@RequestMapping("/api/produtos")
public class ProdutoRestController {

    private final ProdutoRepository produtoRepo;
    private final CategoriaRepository categoriaRepo;
    private final LojaRepository lojaRepo;

    public ProdutoRestController(ProdutoRepository produtoRepo, CategoriaRepository categoriaRepo,
                                 LojaRepository lojaRepo) {
        this.produtoRepo = produtoRepo;
        this.categoriaRepo = categoriaRepo;
        this.lojaRepo = lojaRepo;
    }

    @GetMapping
    public List<Produto> listar(@RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return produtoRepo.findByNomeContainingIgnoreCase(nome);
        }
        return produtoRepo.findAll();
    }

    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id) {
        return produtoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto nao encontrado: " + id));
    }

    @GetMapping("/categorias/{categoriaId}")
    public List<Produto> listarPorCategoria(@PathVariable Long categoriaId) {
        return produtoRepo.findByCategoriaId(categoriaId);
    }

    @GetMapping("/lojas/{lojaId}")
    public List<Produto> listarPorLoja(@PathVariable Long lojaId) {
        return produtoRepo.findByLojaId(lojaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto criar(@Valid @RequestBody Produto produto) {
        produto.setCategoria(resolverCategoria(produto.getCategoria()));
        produto.setLoja(resolverLoja(produto.getLoja()));
        produto.setId(null);
        return produtoRepo.save(produto);
    }

    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @Valid @RequestBody Produto dados) {
        Produto existente = produtoRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto nao encontrado: " + id));
        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        existente.setPreco(dados.getPreco());
        existente.setEstoque(dados.getEstoque());
        if (dados.getCategoria() != null) {
            existente.setCategoria(resolverCategoria(dados.getCategoria()));
        }
        if (dados.getLoja() != null) {
            existente.setLoja(resolverLoja(dados.getLoja()));
        }
        return produtoRepo.save(existente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        if (!produtoRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado: " + id);
        }
        produtoRepo.deleteById(id);
    }

    private Categoria resolverCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id da categoria");
        }
        return categoriaRepo.findById(categoria.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Categoria inexistente: " + categoria.getId()));
    }

    private Loja resolverLoja(Loja loja) {
        if (loja == null || loja.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o id da loja");
        }
        return lojaRepo.findById(loja.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Loja inexistente: " + loja.getId()));
    }
}
