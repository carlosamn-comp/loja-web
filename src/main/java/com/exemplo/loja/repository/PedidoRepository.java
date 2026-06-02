package com.exemplo.loja.repository;

import com.exemplo.loja.model.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DAO da entidade Pedido, aderente a JPA via Spring Data.
 *
 * As consultas usam @EntityGraph para carregar cliente + itens + produto +
 * categoria + loja numa unica busca, evitando LazyInitializationException
 * (open-in-view=false; produto.categoria e produto.loja sao EAGER).
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto",
            "itens.produto.categoria", "itens.produto.loja"})
    List<Pedido> findByClienteIdOrderByDataDesc(Long clienteId);

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto",
            "itens.produto.categoria", "itens.produto.loja"})
    List<Pedido> findByStatusOrderByDataDesc(Pedido.Status status);

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto",
            "itens.produto.categoria", "itens.produto.loja"})
    List<Pedido> findAllByOrderByDataDesc();

    /** Pedidos que contem ao menos um produto da loja informada (vendas da loja). */
    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto",
            "itens.produto.categoria", "itens.produto.loja"})
    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.itens i "
            + "WHERE i.produto.loja.id = :lojaId ORDER BY p.data DESC")
    List<Pedido> findByLojaIdOrderByDataDesc(@Param("lojaId") Long lojaId);

    // Consultas simples (sem grafo) usadas pela REST-API
    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(Pedido.Status status);
}
