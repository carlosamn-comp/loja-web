package com.exemplo.loja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal do sistema Web (Loja / E-commerce).
 *
 * A carga inicial de dados (categorias, produtos, administrador e cliente de
 * demonstracao) e realizada pela classe {@link com.exemplo.loja.config.DataSeeder},
 * executada automaticamente na inicializacao da aplicacao.
 */
@SpringBootApplication
public class LojaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(LojaWebApplication.class, args);
    }
}
