package com.exemplo.loja.web;

import com.exemplo.loja.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Spring MVC que renderiza a listagem de produtos via Thymeleaf.
 */
@Controller
public class ProdutoController {

    private final ProdutoRepository produtoRepo;

    public ProdutoController(ProdutoRepository produtoRepo) {
        this.produtoRepo = produtoRepo;
    }

    @GetMapping("/produtos")
    public String listarProdutos(@RequestParam(required = false) String busca, Model model) {
        if (busca != null && !busca.isBlank()) {
            model.addAttribute("produtos", produtoRepo.findByNomeContainingIgnoreCase(busca));
        } else {
            model.addAttribute("produtos", produtoRepo.findAll());
        }
        model.addAttribute("busca", busca);
        return "produtos";
    }
}
