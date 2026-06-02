package com.exemplo.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Loja / Vendedor do marketplace. Atua como usuario com perfil ROLE_LOJA no
 * Spring Security e e dona dos produtos que cadastra. Um cliente pode comprar
 * produtos de varias lojas.
 */
@Entity
@Table(name = "loja")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** Senha (hash BCrypt). WRITE_ONLY: recebida em JSON, nunca devolvida. */
    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String senha;

    /** CNPJ apenas obrigatorio e unico (sem validacao de formato — e um modelo). */
    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String cnpj;

    @Column(length = 255)
    private String descricao;

    @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Produto> produtos = new ArrayList<>();

    public Loja() {
    }

    public Loja(String nome, String email, String senha, String cnpj, String descricao) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cnpj = cnpj;
        this.descricao = descricao;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    @Override
    public String toString() {
        return "Loja{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }
}
