package com.exemplo.loja.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * Corpo da requisicao para criar um pedido via REST-API.
 */
public class PedidoRequest {

    @NotEmpty
    @Valid
    private List<ItemPedidoRequest> itens = new ArrayList<>();

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
}
