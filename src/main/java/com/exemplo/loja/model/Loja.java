package com.exemplo.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Loja / Vendedor do marketplace. Subclasse de {@link Usuario} (herda nome, email,
 * senha e role). O construtor define a role como "LOJA" (perfil ROLE_LOJA). E dona
 * dos produtos que cadastra; um cliente pode comprar de varias lojas.
 */
@Entity
@DiscriminatorValue("LOJA")
public class Loja extends Usuario {

    /** CNPJ apenas obrigatorio e unico (sem validacao de formato — e um modelo). */
    @NotBlank
    @Column(unique = true, length = 20)
    private String cnpj;

    @Column(length = 255)
    private String descricao;

    @OneToMany(mappedBy = "loja", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Produto> produtos = new ArrayList<>();

    public Loja() {
        super();
        setRole("LOJA"); // renomeia a role base "USER" -> "LOJA"
    }

    public Loja(String nome, String email, String senha, String cnpj, String descricao) {
        super(nome, email, senha);  // role inicia como "USER"
        setRole("LOJA");            // renomeia para "LOJA"
        this.cnpj = cnpj;
        this.descricao = descricao;
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
        return "Loja{id=" + getId() + ", nome='" + getNome() + "', email='" + getEmail() + "'}";
    }
}
