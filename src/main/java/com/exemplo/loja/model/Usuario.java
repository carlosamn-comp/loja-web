package com.exemplo.loja.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Classe-mae (entidade abstrata) de todos os usuarios autenticaveis que ficam no
 * banco: {@link Loja} e {@link Cliente}. (O administrador NAO e um Usuario do
 * banco — fica definido diretamente em {@code UsuarioDetailsService}.)
 *
 * Usa HERANCA JPA com a estrategia SINGLE_TABLE: todas as subclasses sao gravadas
 * numa unica tabela "usuario", diferenciadas pela coluna discriminadora "tipo".
 * Vantagens: consultas rapidas (sem JOIN) e o e-mail fica unico entre TODOS os
 * usuarios (loja e cliente) automaticamente, por ser uma so tabela.
 *
 * O campo {@code role} (ROLE do Spring Security) e definido pelo CONSTRUTOR de
 * cada subclasse — "LOJA" em {@link Loja} e "CLIENTE" em {@link Cliente} — e e
 * lido diretamente no login, evitando descobrir o perfil por tentativa e erro.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class Usuario {

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

    /** Perfil do Spring Security ("LOJA" ou "CLIENTE"), definido no construtor da subclasse. */
    @Column(nullable = false, length = 20)
    private String role;

    protected Usuario() {
    }

    protected Usuario(String nome, String email, String senha, String role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
