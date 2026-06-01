package com.exemplo.loja;

import com.exemplo.loja.model.Categoria;
import com.exemplo.loja.model.Cliente;
import com.exemplo.loja.model.ItemPedido;
import com.exemplo.loja.model.Pedido;
import com.exemplo.loja.model.Produto;
import com.exemplo.loja.repository.CategoriaRepository;
import com.exemplo.loja.repository.ClienteRepository;
import com.exemplo.loja.repository.PedidoRepository;
import com.exemplo.loja.repository.ProdutoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Classe principal do sistema Web.
 *
 * Alem de iniciar a aplicacao Spring MVC, o bean {@link CommandLineRunner}
 * demonstra as operacoes basicas de CRUD (Create, Read, Update, Delete)
 * sobre algumas instancias das entidades, conforme exigido na atividade T5.
 */
@SpringBootApplication
public class LojaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(LojaWebApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoCrud(CategoriaRepository categoriaRepo,
                                      ProdutoRepository produtoRepo,
                                      ClienteRepository clienteRepo,
                                      PedidoRepository pedidoRepo) {
        return args -> {
            System.out.println("\n================ DEMONSTRACAO DO CRUD (T5) ================\n");

            // ---------- CREATE ----------
            System.out.println(">> CREATE: inserindo categorias e produtos");
            Categoria eletronicos = categoriaRepo.save(new Categoria("Eletronicos"));
            Categoria livros = categoriaRepo.save(new Categoria("Livros"));

            Produto notebook = produtoRepo.save(
                    new Produto("Notebook Dell", "Notebook 16GB RAM, SSD 512GB",
                            new BigDecimal("4500.00"), 10, eletronicos));
            Produto fone = produtoRepo.save(
                    new Produto("Fone Bluetooth", "Fone over-ear com cancelamento de ruido",
                            new BigDecimal("350.00"), 50, eletronicos));
            Produto livroJava = produtoRepo.save(
                    new Produto("Java Efetivo", "Boas praticas de programacao em Java",
                            new BigDecimal("180.00"), 30, livros));

            System.out.println("   Categorias salvas: " + categoriaRepo.count());
            System.out.println("   Produtos salvos:   " + produtoRepo.count());

            // ---------- CREATE: cliente e pedido com itens ----------
            System.out.println("\n>> CREATE: inserindo cliente e pedido com itens");
            Cliente cliente = clienteRepo.save(new Cliente("Maria Silva", "maria@email.com"));

            Pedido pedido = new Pedido(cliente);
            pedido.adicionarItem(new ItemPedido(notebook, 1));
            pedido.adicionarItem(new ItemPedido(fone, 2));
            pedido = pedidoRepo.save(pedido);
            System.out.println("   Pedido criado: " + pedido);

            // ---------- READ ----------
            System.out.println("\n>> READ: listando todos os produtos");
            produtoRepo.findAll().forEach(p -> System.out.println("   - " + p));

            System.out.println("\n>> READ: buscando produto por id = " + notebook.getId());
            produtoRepo.findById(notebook.getId())
                    .ifPresent(p -> System.out.println("   Encontrado: " + p));

            System.out.println("\n>> READ: query method - produtos da categoria 'Eletronicos'");
            List<Produto> eletronicosList = produtoRepo.buscarPorNomeCategoria("Eletronicos");
            eletronicosList.forEach(p -> System.out.println("   - " + p));

            System.out.println("\n>> READ: query method - produtos com 'java' no nome");
            produtoRepo.findByNomeContainingIgnoreCase("java")
                    .forEach(p -> System.out.println("   - " + p));

            // ---------- UPDATE ----------
            System.out.println("\n>> UPDATE: alterando preco e estoque do notebook");
            notebook.setPreco(new BigDecimal("4299.90"));
            notebook.setEstoque(8);
            produtoRepo.save(notebook);
            produtoRepo.findById(notebook.getId())
                    .ifPresent(p -> System.out.println("   Atualizado: " + p));

            System.out.println("\n>> UPDATE: marcando pedido como PAGO");
            pedido.setStatus(Pedido.Status.PAGO);
            pedido = pedidoRepo.save(pedido);
            System.out.println("   Atualizado: " + pedido);

            // ---------- DELETE ----------
            // Observacao: 'notebook' e 'fone' estao vinculados a itens do pedido,
            // entao remove-los violaria a integridade referencial (FK em item_pedido).
            // Para demonstrar o DELETE, removemos um produto sem pedidos associados.
            System.out.println("\n>> DELETE: removendo o produto 'Java Efetivo' (sem pedidos)");
            produtoRepo.deleteById(livroJava.getId());
            System.out.println("   Produtos restantes: " + produtoRepo.count());
            produtoRepo.findAll().forEach(p -> System.out.println("   - " + p));

            System.out.println("\n=================== FIM DA DEMONSTRACAO ===================");
            System.out.println("Console H2 disponivel em: http://localhost:8080/h2-console");
            System.out.println("Pagina Web (Thymeleaf):    http://localhost:8080/produtos");
            System.out.println("REST-API de produtos:      http://localhost:8080/api/produtos\n");
        };
    }
}
