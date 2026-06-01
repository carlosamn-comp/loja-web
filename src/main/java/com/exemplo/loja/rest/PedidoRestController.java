package com.exemplo.loja.rest;

import com.exemplo.loja.dto.PedidoRequest;
import com.exemplo.loja.model.Pedido;
import com.exemplo.loja.repository.PedidoRepository;
import com.exemplo.loja.service.PedidoService;
import com.exemplo.loja.web.carrinho.ItemCarrinho;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-API de pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    private final PedidoRepository pedidoRepo;
    private final PedidoService pedidoService;

    public PedidoRestController(PedidoRepository pedidoRepo, PedidoService pedidoService) {
        this.pedidoRepo = pedidoRepo;
        this.pedidoService = pedidoService;
    }

    /** Cria um novo pedido para o cliente informado. */
    @PostMapping("/clientes/{clienteId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido criar(@PathVariable Long clienteId, @Valid @RequestBody PedidoRequest request) {
        List<ItemCarrinho> itens = request.getItens().stream()
                .map(i -> new ItemCarrinho(i.getProdutoId(), null, null, i.getQuantidade()))
                .toList();
        return pedidoService.checkout(clienteId, itens);
    }

    /** Retorna a lista de pedidos do cliente informado. */
    @GetMapping("/clientes/{clienteId}")
    public List<Pedido> listarPorCliente(@PathVariable Long clienteId) {
        return pedidoRepo.findByClienteIdOrderByDataDesc(clienteId);
    }

    /** Retorna a lista de pedidos com o status informado. */
    @GetMapping("/status/{status}")
    public List<Pedido> listarPorStatus(@PathVariable Pedido.Status status) {
        return pedidoRepo.findByStatusOrderByDataDesc(status);
    }
}
