package com.exemplo.loja.web;

import com.exemplo.loja.model.Produto;
import com.exemplo.loja.service.CategoriaService;
import com.exemplo.loja.service.ProdutoService;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Catalogo publico de produtos (R4). Nao requer login. Permite filtrar por
 * nome, categoria e faixa de preco (filtros combinaveis e opcionais).
 */
@Controller
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public ProdutoController(ProdutoService produtoService, CategoriaService categoriaService) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/produtos";
    }

    @GetMapping("/produtos")
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) Long categoriaId,
                         @RequestParam(required = false) BigDecimal precoMin,
                         @RequestParam(required = false) BigDecimal precoMax,
                         Model model) {
        List<Produto> produtos = produtoService.filtrar(nome, categoriaId, precoMin, precoMax);
        Map<Long, List<Long>> imagensPorProduto = new LinkedHashMap<>();
        for (Produto p : produtos) {
            imagensPorProduto.put(p.getId(), produtoService.listarImagemIds(p.getId()));
        }
        model.addAttribute("produtos", produtos);
        model.addAttribute("imagensPorProduto", imagensPorProduto);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("nome", nome);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("precoMin", precoMin);
        model.addAttribute("precoMax", precoMax);
        return "produtos";
    }
}
