package com.exemplo.loja.exception;

/**
 * Lancada quando a quantidade pedida de um produto excede o estoque disponivel.
 */
public class EstoqueInsuficienteException extends RuntimeException {

    private final String produtoNome;

    public EstoqueInsuficienteException(String produtoNome) {
        super("Estoque insuficiente para o produto: " + produtoNome);
        this.produtoNome = produtoNome;
    }

    public String getProdutoNome() {
        return produtoNome;
    }
}
