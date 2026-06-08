package com.exemplo.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente da loja. Subclasse de {@link Usuario} (herda nome, email, senha e role).
 * O construtor define a role como "CLIENTE" (perfil ROLE_CLIENTE). Lado "um" do
 * relacionamento 1:N com Pedido.
 *
 * Como usa SINGLE_TABLE, os campos especificos do cliente (cpf, sexo, etc.) ficam
 * na tabela "usuario" e sao nulos nas linhas que sao lojas — por isso o banco os
 * mantem como colunas anulaveis (a obrigatoriedade vale via Bean Validation).
 */
@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    /** CPF apenas obrigatorio e unico (sem validacao de formato — e um modelo). */
    @NotBlank
    @Column(unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Sexo sexo;

    @NotNull
    @Past
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Pedido> pedidos = new ArrayList<>();

    public Cliente() {
        super();
        setRole("CLIENTE");
    }

    public Cliente(String nome, String email, String senha, String cpf,
                   String telefone, Sexo sexo, LocalDate dataNascimento) {
        super(nome, email, senha, "CLIENTE");
        this.cpf = cpf;
        this.telefone = telefone;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public String toString() {
        return "Cliente{id=" + getId() + ", nome='" + getNome() + "', email='" + getEmail() + "'}";
    }
}
