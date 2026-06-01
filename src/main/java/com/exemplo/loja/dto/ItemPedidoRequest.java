package com.exemplo.loja.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Item de um pedido recebido pela REST-API (produto + quantidade).
 */
public class ItemPedidoRequest {

    @NotNull
    private Long produtoId;

    @Positive
    private int quantidade;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
