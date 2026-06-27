package com.exemplo.loja.client;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO usado pela aplicacao CLIENTE da REST-API (T8). Representa uma categoria
 * trafegada em JSON entre o cliente e a API. Note que o cliente REST NAO depende
 * das entidades JPA — ele conhece apenas este formato de dados da API.
 */
public class CategoriaDto {

    private Long id;

    @NotBlank
    private String nome;

    private String descricao;

    public CategoriaDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
