package com.exemplo.loja.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Administrador da plataforma. Subclasse de {@link Usuario} (herda nome, email,
 * senha e role), persistida na tabela `usuario` com `tipo = ADMIN`. O construtor
 * RENOMEIA a role base "USER" para "ADMIN".
 *
 * A senha e armazenada criptografada (BCrypt), como a dos demais usuarios.
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Administrador extends Usuario {

    public Administrador() {
        super();
        setRole("ADMIN");
    }

    public Administrador(String nome, String email, String senha) {
        super(nome, email, senha);  // role inicia como "USER"
        setRole("ADMIN");           // renomeia para "ADMIN"
    }
}
