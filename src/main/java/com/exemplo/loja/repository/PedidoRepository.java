package com.exemplo.loja.repository;

import com.exemplo.loja.model.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO da entidade Pedido, aderente a JPA via Spring Data.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByStatus(Pedido.Status status);
}
