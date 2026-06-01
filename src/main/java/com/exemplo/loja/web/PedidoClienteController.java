package com.exemplo.loja.web;

import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.repository.PedidoRepository;
import com.exemplo.loja.service.ClienteService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Listagem dos pedidos do cliente autenticado (R6). Requer perfil CLIENTE.
 */
@Controller
public class PedidoClienteController {

    private final PedidoRepository pedidoRepo;
    private final ClienteService clienteService;

    public PedidoClienteController(PedidoRepository pedidoRepo, ClienteService clienteService) {
        this.pedidoRepo = pedidoRepo;
        this.clienteService = clienteService;
    }

    @GetMapping("/pedidos")
    public String meusPedidos(Principal principal, Model model) {
        Cliente cliente = clienteService.buscarPorEmail(principal.getName());
        model.addAttribute("pedidos", pedidoRepo.findByClienteIdOrderByDataDesc(cliente.getId()));
        return "pedidos/meus-pedidos";
    }
}
