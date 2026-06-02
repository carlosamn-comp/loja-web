package com.exemplo.loja.web.loja;

import com.exemplo.loja.model.Loja;
import com.exemplo.loja.model.Pedido;
import com.exemplo.loja.repository.PedidoRepository;
import com.exemplo.loja.service.LojaService;
import com.exemplo.loja.service.PedidoService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Vendas da loja (R7): lista os pedidos que contem produtos da loja logada e
 * permite atualizar o status (notificando o cliente). Requer perfil LOJA.
 */
@Controller
@RequestMapping("/loja/pedidos")
public class PedidoLojaController {

    private final PedidoRepository pedidoRepo;
    private final PedidoService pedidoService;
    private final LojaService lojaService;

    public PedidoLojaController(PedidoRepository pedidoRepo, PedidoService pedidoService,
                                LojaService lojaService) {
        this.pedidoRepo = pedidoRepo;
        this.pedidoService = pedidoService;
        this.lojaService = lojaService;
    }

    @GetMapping
    public String listar(Principal principal, Model model) {
        Loja loja = lojaService.buscarPorEmail(principal.getName());
        model.addAttribute("pedidos", pedidoRepo.findByLojaIdOrderByDataDesc(loja.getId()));
        model.addAttribute("statusList", Pedido.Status.values());
        return "loja/pedidos/lista";
    }

    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam Pedido.Status novoStatus,
                                  Principal principal, RedirectAttributes redirect) {
        Loja loja = lojaService.buscarPorEmail(principal.getName());
        // Autorizacao: o pedido precisa conter algum produto desta loja
        boolean pertence = pedidoRepo.findByLojaIdOrderByDataDesc(loja.getId()).stream()
                .anyMatch(p -> p.getId().equals(id));
        if (!pertence) {
            return "redirect:/loja/pedidos";
        }
        try {
            pedidoService.atualizarStatus(id, novoStatus);
            redirect.addFlashAttribute("ok", "admin.pedidos.atualizado");
        } catch (IllegalStateException ex) {
            redirect.addFlashAttribute("erro", "admin.pedidos.transicaoInvalida");
        }
        return "redirect:/loja/pedidos";
    }
}
