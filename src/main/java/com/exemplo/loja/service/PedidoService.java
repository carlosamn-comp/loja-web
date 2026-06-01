package com.exemplo.loja.service;

import com.exemplo.loja.exception.EstoqueInsuficienteException;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.ItemPedido;
import com.exemplo.loja.model.Pedido;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.repository.ClienteRepository;
import com.exemplo.loja.repository.PedidoRepository;
import com.exemplo.loja.repository.ProdutoRepository;
import com.exemplo.loja.web.carrinho.ItemCarrinho;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negocio dos pedidos: checkout do carrinho (R5) e atualizacao de
 * status pela administracao (R7). As operacoes sao transacionais; o e-mail
 * (simulado) e disparado com os dados ja carregados dentro da transacao.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepo;
    private final ProdutoRepository produtoRepo;
    private final ClienteRepository clienteRepo;
    private final EmailService emailService;

    public PedidoService(PedidoRepository pedidoRepo, ProdutoRepository produtoRepo,
                        ClienteRepository clienteRepo, EmailService emailService) {
        this.pedidoRepo = pedidoRepo;
        this.produtoRepo = produtoRepo;
        this.clienteRepo = clienteRepo;
        this.emailService = emailService;
    }

    /**
     * Cria um pedido a partir dos itens do carrinho do cliente: rele o preco
     * atual de cada produto, valida e decrementa o estoque, e notifica o cliente.
     */
    @Transactional
    public Pedido checkout(Long clienteId, List<ItemCarrinho> itensCarrinho) {
        Cliente cliente = clienteRepo.findById(clienteId).orElseThrow(
                () -> new IllegalArgumentException("Cliente nao encontrado: " + clienteId));

        Pedido pedido = new Pedido(cliente);
        for (ItemCarrinho ic : itensCarrinho) {
            Produto produto = produtoRepo.findById(ic.getProdutoId()).orElseThrow(
                    () -> new IllegalArgumentException("Produto nao encontrado: " + ic.getProdutoId()));

            if (produto.getEstoque() < ic.getQuantidade()) {
                throw new EstoqueInsuficienteException(produto.getNome());
            }

            // precoUnitario e definido pelo preco atual do produto (lido do banco)
            pedido.adicionarItem(new ItemPedido(produto, ic.getQuantidade()));
            produto.setEstoque(produto.getEstoque() - ic.getQuantidade());
        }

        Pedido salvo = pedidoRepo.save(pedido);
        emailService.enviarConfirmacaoPedido(salvo); // ainda dentro da transacao
        return salvo;
    }

    /** Transicoes de status permitidas. */
    private static final Set<Pedido.Status> A_PARTIR_DE_ABERTO =
            EnumSet.of(Pedido.Status.PAGO, Pedido.Status.CANCELADO);
    private static final Set<Pedido.Status> A_PARTIR_DE_PAGO =
            EnumSet.of(Pedido.Status.ENVIADO, Pedido.Status.CANCELADO);

    @Transactional
    public Pedido atualizarStatus(Long pedidoId, Pedido.Status novoStatus) {
        Pedido pedido = pedidoRepo.findById(pedidoId).orElseThrow(
                () -> new IllegalArgumentException("Pedido nao encontrado: " + pedidoId));

        validarTransicao(pedido.getStatus(), novoStatus);
        pedido.setStatus(novoStatus);
        Pedido salvo = pedidoRepo.save(pedido);
        emailService.enviarAtualizacaoStatus(salvo);
        return salvo;
    }

    private void validarTransicao(Pedido.Status atual, Pedido.Status novo) {
        if (atual == novo) {
            return;
        }
        boolean permitido = switch (atual) {
            case ABERTO -> A_PARTIR_DE_ABERTO.contains(novo);
            case PAGO -> A_PARTIR_DE_PAGO.contains(novo);
            case ENVIADO, CANCELADO -> false;
        };
        if (!permitido) {
            throw new IllegalStateException(
                    "Transicao de status invalida: " + atual + " -> " + novo);
        }
    }
}
