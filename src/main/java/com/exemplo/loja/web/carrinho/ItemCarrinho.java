package com.exemplo.loja.web.carrinho;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Item leve do carrinho mantido em sessao (nao e uma entidade JPA).
 */
public class ItemCarrinho implements Serializable {

    private Long produtoId;
    private String nome;
    private BigDecimal precoUnitario;
    private int quantidade;

    public ItemCarrinho() {
    }

    public ItemCarrinho(Long produtoId, String nome, BigDecimal precoUnitario, int quantidade) {
        this.produtoId = produtoId;
        this.nome = nome;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        if (precoUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
