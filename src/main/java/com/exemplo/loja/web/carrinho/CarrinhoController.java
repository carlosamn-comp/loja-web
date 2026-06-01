package com.exemplo.loja.web.carrinho;

import com.exemplo.loja.exception.EstoqueInsuficienteException;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.service.ClienteService;
import com.exemplo.loja.service.PedidoService;
import com.exemplo.loja.service.ProdutoService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Carrinho e finalizacao de compra (R5). Requer perfil CLIENTE.
 */
@Controller
public class CarrinhoController {

    private final CarrinhoSession carrinho;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public CarrinhoController(CarrinhoSession carrinho, ProdutoService produtoService,
                             ClienteService clienteService, PedidoService pedidoService) {
        this.carrinho = carrinho;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/carrinho")
    public String ver(Model model) {
        model.addAttribute("carrinho", carrinho);
        return "carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionar(@RequestParam Long produtoId,
                           @RequestParam(defaultValue = "1") int quantidade,
                           RedirectAttributes redirect) {
        Produto produto = produtoService.buscar(produtoId);
        int qtd = Math.max(1, quantidade);
        carrinho.adicionar(new ItemCarrinho(produto.getId(), produto.getNome(),
                produto.getPreco(), qtd));
        redirect.addFlashAttribute("ok", "carrinho.adicionado");
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String remover(@RequestParam Long produtoId) {
        carrinho.remover(produtoId);
        return "redirect:/carrinho";
    }

    @PostMapping("/checkout")
    public String checkout(Principal principal, RedirectAttributes redirect) {
        if (carrinho.isVazio()) {
            return "redirect:/carrinho";
        }
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        try {
            pedidoService.checkout(cliente.getId(), carrinho.getItens());
            carrinho.limpar();
            redirect.addFlashAttribute("ok", "pedido.realizado");
            return "redirect:/pedidos";
        } catch (EstoqueInsuficienteException ex) {
            redirect.addFlashAttribute("erroEstoque", ex.getProdutoNome());
            return "redirect:/carrinho";
        }
    }
}
