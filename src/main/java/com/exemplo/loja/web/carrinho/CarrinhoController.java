package com.exemplo.loja.web.carrinho;

import com.exemplo.loja.exception.EstoqueInsuficienteException;
import com.exemplo.loja.model.CarrinhoItem;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.repository.CarrinhoItemRepository;
import com.exemplo.loja.service.ClienteService;
import com.exemplo.loja.service.PedidoService;
import com.exemplo.loja.service.ProdutoService;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Carrinho e finalizacao de compra (R5). Requer perfil CLIENTE.
 *
 * O carrinho e PERSISTIDO no banco (entidade {@link CarrinhoItem}, uma linha por
 * cliente+produto), entao os itens permanecem mesmo que o cliente feche e reabra
 * a pagina/navegador, e ate entre logins.
 */
@Controller
public class CarrinhoController {

    private final CarrinhoItemRepository carrinhoRepo;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public CarrinhoController(CarrinhoItemRepository carrinhoRepo, ProdutoService produtoService,
                             ClienteService clienteService, PedidoService pedidoService) {
        this.carrinhoRepo = carrinhoRepo;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    private Cliente clienteLogado(Principal principal) {
        return clienteService.buscarPorEmail(principal.getName());
    }

    @GetMapping("/carrinho")
    public String ver(Principal principal, Model model) {
        Cliente cliente = clienteLogado(principal);
        List<CarrinhoItem> itens = carrinhoRepo.findByClienteId(cliente.getId());
        BigDecimal total = itens.stream()
                .map(CarrinhoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("itens", itens);
        model.addAttribute("total", total);
        return "carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    @Transactional
    public String adicionar(@RequestParam Long produtoId,
                           @RequestParam(defaultValue = "1") int quantidade,
                           Principal principal, RedirectAttributes redirect) {
        Cliente cliente = clienteLogado(principal);
        Produto produto = produtoService.buscar(produtoId);
        int qtd = Math.max(1, quantidade);

        carrinhoRepo.findByClienteIdAndProdutoId(cliente.getId(), produtoId)
                .ifPresentOrElse(
                        item -> item.setQuantidade(item.getQuantidade() + qtd),   // ja existe: soma
                        () -> carrinhoRepo.save(new CarrinhoItem(cliente, produto, qtd))); // novo

        redirect.addFlashAttribute("ok", "carrinho.adicionado");
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover")
    @Transactional
    public String remover(@RequestParam Long produtoId, Principal principal) {
        Cliente cliente = clienteLogado(principal);
        carrinhoRepo.findByClienteIdAndProdutoId(cliente.getId(), produtoId)
                .ifPresent(carrinhoRepo::delete);
        return "redirect:/carrinho";
    }

    @PostMapping("/checkout")
    @Transactional
    public String checkout(Principal principal, RedirectAttributes redirect) {
        Cliente cliente = clienteLogado(principal);
        List<CarrinhoItem> itens = carrinhoRepo.findByClienteId(cliente.getId());
        if (itens.isEmpty()) {
            return "redirect:/carrinho";
        }
        // converte os itens do carrinho (banco) para o formato leve do checkout
        List<ItemCarrinho> paraPedido = itens.stream()
                .map(i -> new ItemCarrinho(i.getProduto().getId(), null, null, i.getQuantidade()))
                .toList();
        try {
            pedidoService.checkout(cliente.getId(), paraPedido);
            carrinhoRepo.deleteAll(itens);   // esvazia o carrinho apos a compra
            redirect.addFlashAttribute("ok", "pedido.realizado");
            return "redirect:/pedidos";
        } catch (EstoqueInsuficienteException ex) {
            redirect.addFlashAttribute("erroEstoque", ex.getProdutoNome());
            return "redirect:/carrinho";
        }
    }
}
