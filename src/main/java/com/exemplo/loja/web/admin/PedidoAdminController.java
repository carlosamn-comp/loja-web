package com.exemplo.loja.web.admin;

import com.exemplo.loja.model.Pedido;
import com.exemplo.loja.repository.PedidoRepository;
import com.exemplo.loja.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Gestao de pedidos pela administracao (R7): listagem com filtro por status e
 * atualizacao do status (que notifica o cliente por e-mail). Requer perfil ADMIN.
 */
@Controller
@RequestMapping("/admin/pedidos")
public class PedidoAdminController {

    private final PedidoRepository pedidoRepo;
    private final PedidoService pedidoService;

    public PedidoAdminController(PedidoRepository pedidoRepo, PedidoService pedidoService) {
        this.pedidoRepo = pedidoRepo;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Pedido.Status status, Model model) {
        model.addAttribute("pedidos", status != null
                ? pedidoRepo.findByStatusOrderByDataDesc(status)
                : pedidoRepo.findAllByOrderByDataDesc());
        model.addAttribute("statusList", Pedido.Status.values());
        model.addAttribute("statusSelecionado", status);
        return "admin/pedidos/lista";
    }

    @PostMapping("/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam Pedido.Status novoStatus,
                                  RedirectAttributes redirect) {
        try {
            pedidoService.atualizarStatus(id, novoStatus);
            redirect.addFlashAttribute("ok", "admin.pedidos.atualizado");
        } catch (IllegalStateException ex) {
            redirect.addFlashAttribute("erro", "admin.pedidos.transicaoInvalida");
        }
        return "redirect:/admin/pedidos";
    }
}
