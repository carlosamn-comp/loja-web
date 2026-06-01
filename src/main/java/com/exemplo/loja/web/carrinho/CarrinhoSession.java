package com.exemplo.loja.web.carrinho;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Carrinho de compras mantido na sessao do cliente. Guarda apenas dados leves
 * (nao entidades JPA gerenciadas), conforme boa pratica com OSIV desabilitado.
 */
@Component
@SessionScope
public class CarrinhoSession implements Serializable {

    private final List<ItemCarrinho> itens = new ArrayList<>();

    /** Adiciona um item; se o produto ja estiver no carrinho, soma a quantidade. */
    public void adicionar(ItemCarrinho novo) {
        for (ItemCarrinho item : itens) {
            if (item.getProdutoId().equals(novo.getProdutoId())) {
                item.setQuantidade(item.getQuantidade() + novo.getQuantidade());
                return;
            }
        }
        itens.add(novo);
    }

    public void remover(Long produtoId) {
        itens.removeIf(item -> item.getProdutoId().equals(produtoId));
    }

    public void limpar() {
        itens.clear();
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public boolean isVazio() {
        return itens.isEmpty();
    }

    public BigDecimal getTotal() {
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
