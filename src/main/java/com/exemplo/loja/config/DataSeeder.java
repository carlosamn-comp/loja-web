package com.exemplo.loja.config;

import com.exemplo.loja.model.Categoria;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.Loja;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.model.Sexo;
import com.exemplo.loja.repository.CategoriaRepository;
import com.exemplo.loja.repository.ClienteRepository;
import com.exemplo.loja.repository.LojaRepository;
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
 * Carga inicial de dados (marketplace).
 *
 * Popula um cliente, DUAS lojas e produtos de exemplo vinculados a essas lojas.
 * O ADMINISTRADOR nao e semeado aqui: ele e definido diretamente no codigo, em
 * {@link com.exemplo.loja.service.UsuarioDetailsService}. Todas as credenciais
 * usam a senha "123".
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    public CommandLineRunner seed(LojaRepository lojaRepo,
                                  ClienteRepository clienteRepo,
                                  CategoriaRepository categoriaRepo,
                                  ProdutoRepository produtoRepo,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // ---------- Cliente de demonstracao ----------
            if (!clienteRepo.existsByEmail("cliente@loja.com")) {
                clienteRepo.save(new Cliente(
                        "Maria Silva", "cliente@loja.com", passwordEncoder.encode("123"),
                        "111.111.111-11", "(11) 99999-0000", Sexo.FEMININO,
                        LocalDate.of(1995, 5, 20)));
                log.info("Cliente criado: cliente@loja.com / 123");
            }

            // ---------- Lojas / vendedores ----------
            if (!lojaRepo.existsByEmail("loja@loja.com")) {
                lojaRepo.save(new Loja("TechStore", "loja@loja.com", passwordEncoder.encode("123"),
                        "11.111.111/0001-11", "Eletronicos e perifericos"));
                log.info("Loja criada: loja@loja.com / 123");
            }
            if (!lojaRepo.existsByEmail("loja2@loja.com")) {
                lojaRepo.save(new Loja("Livraria Cultura", "loja2@loja.com", passwordEncoder.encode("123"),
                        "22.222.222/0001-22", "Livros tecnicos e de ficcao"));
                log.info("Loja criada: loja2@loja.com / 123");
            }

            // ---------- Categorias e produtos (vinculados as lojas) ----------
            if (categoriaRepo.count() == 0) {
                Categoria eletronicos = categoriaRepo.save(
                        new Categoria("Eletronicos", "Dispositivos e acessorios eletronicos"));
                Categoria livros = categoriaRepo.save(
                        new Categoria("Livros", "Livros tecnicos e de ficcao"));
                Categoria perifericos = categoriaRepo.save(
                        new Categoria("Perifericos", "Mouses, teclados e acessorios"));

                Loja techStore = lojaRepo.findByEmail("loja@loja.com").orElseThrow();
                Loja livraria = lojaRepo.findByEmail("loja2@loja.com").orElseThrow();

                salvarProduto(produtoRepo, "Notebook Dell", "Notebook 16GB RAM, SSD 512GB",
                        new BigDecimal("4500.00"), 10, eletronicos, techStore);
                salvarProduto(produtoRepo, "Fone Bluetooth", "Fone over-ear com cancelamento de ruido",
                        new BigDecimal("350.00"), 50, eletronicos, techStore);
                salvarProduto(produtoRepo, "Mouse Gamer", "Mouse RGB 7 botoes",
                        new BigDecimal("150.00"), 40, perifericos, techStore);
                salvarProduto(produtoRepo, "Java Efetivo", "Boas praticas de programacao em Java",
                        new BigDecimal("180.00"), 30, livros, livraria);
                salvarProduto(produtoRepo, "O Senhor dos Aneis", "Edicao completa",
                        new BigDecimal("120.00"), 25, livros, livraria);

                log.info("Catalogo populado: {} categorias, {} produtos, {} lojas",
                        categoriaRepo.count(), produtoRepo.count(), lojaRepo.count());
            }

            log.info("===== Carga inicial (marketplace) concluida =====");
            log.info("Admin (fixo no codigo): admin@loja.com / 123");
            log.info("Catalogo publico:  http://localhost:8080/produtos");
        };
    }

    private void salvarProduto(ProdutoRepository repo, String nome, String descricao,
                               BigDecimal preco, int estoque, Categoria categoria, Loja loja) {
        Produto p = new Produto(nome, descricao, preco, estoque, categoria);
        p.setLoja(loja);
        repo.save(p);
    }
}
