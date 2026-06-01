package com.exemplo.loja.repository;

import com.exemplo.loja.model.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO da entidade Pedido, aderente a JPA via Spring Data.
 *
 * As consultas usam @EntityGraph para carregar cliente + itens + produtos numa
 * unica busca, evitando LazyInitializationException nas telas (open-in-view=false).
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto", "itens.produto.categoria"})
    List<Pedido> findByClienteIdOrderByDataDesc(Long clienteId);

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto", "itens.produto.categoria"})
    List<Pedido> findByStatusOrderByDataDesc(Pedido.Status status);

    @EntityGraph(attributePaths = {"cliente", "itens", "itens.produto", "itens.produto.categoria"})
    List<Pedido> findAllByOrderByDataDesc();

    // Consultas simples (sem grafo) usadas pela REST-API
    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(Pedido.Status status);
}
