package com.exemplo.loja.config;

import com.exemplo.loja.model.Administrador;
import com.exemplo.loja.model.Categoria;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.model.Sexo;
import com.exemplo.loja.repository.AdministradorRepository;
import com.exemplo.loja.repository.CategoriaRepository;
import com.exemplo.loja.repository.ClienteRepository;
import com.exemplo.loja.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Carga inicial de dados executada na inicializacao da aplicacao.
 *
 * Popula o administrador (login + senha com BCrypt), um cliente de demonstracao,
 * categorias e produtos de exemplo, de modo que o sistema ja suba navegavel.
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seed(AdministradorRepository administradorRepo,
                                  ClienteRepository clienteRepo,
                                  CategoriaRepository categoriaRepo,
                                  ProdutoRepository produtoRepo,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // ---------- Administrador (login populado na inicializacao) ----------
            if (!administradorRepo.existsByEmail("admin@loja.com")) {
                administradorRepo.save(new Administrador(
                        "Administrador",
                        "admin@loja.com",
                        passwordEncoder.encode("123")));
                log.info("Administrador criado: admin@loja.com / 123");
            }

            // ---------- Cliente de demonstracao ----------
            if (!clienteRepo.existsByEmail("cliente@loja.com")) {
                clienteRepo.save(new Cliente(
                        "Maria Silva",
                        "cliente@loja.com",
                        passwordEncoder.encode("123"),
                        "529.982.247-25",
                        "(11) 99999-0000",
                        Sexo.FEMININO,
                        LocalDate.of(1995, 5, 20)));
                log.info("Cliente de demonstracao criado: cliente@loja.com / 123");
            }

            // ---------- Categorias e produtos ----------
            if (categoriaRepo.count() == 0) {
                Categoria eletronicos = categoriaRepo.save(
                        new Categoria("Eletronicos", "Dispositivos e acessorios eletronicos"));
                Categoria livros = categoriaRepo.save(
                        new Categoria("Livros", "Livros tecnicos e de ficcao"));
                Categoria perifericos = categoriaRepo.save(
                        new Categoria("Perifericos", "Mouses, teclados e acessorios"));

                produtoRepo.save(new Produto("Notebook Dell",
                        "Notebook 16GB RAM, SSD 512GB", new BigDecimal("4500.00"), 10, eletronicos));
                produtoRepo.save(new Produto("Fone Bluetooth",
                        "Fone over-ear com cancelamento de ruido", new BigDecimal("350.00"), 50, eletronicos));
                produtoRepo.save(new Produto("Java Efetivo",
                        "Boas praticas de programacao em Java", new BigDecimal("180.00"), 30, livros));
                produtoRepo.save(new Produto("Mouse Gamer",
                        "Mouse RGB 7 botoes", new BigDecimal("150.00"), 40, perifericos));

                log.info("Catalogo populado: {} categorias, {} produtos",
                        categoriaRepo.count(), produtoRepo.count());
            }

            log.info("===== Carga inicial concluida =====");
            log.info("Catalogo publico:  http://localhost:8080/produtos");
            log.info("Login:             http://localhost:8080/login");
            log.info("Console H2:        http://localhost:8080/h2-console (URL jdbc:h2:mem:lojadb)");
        };
    }
}
