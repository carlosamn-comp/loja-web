package com.exemplo.loja.repository;

import com.exemplo.loja.model.CarrinhoItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DAO do carrinho persistente. Carrega o produto (e sua categoria/loja) junto,
 * para exibir o carrinho sem LazyInitializationException (open-in-view=false).
 */
@Repository
public interface CarrinhoItemRepository extends JpaRepository<CarrinhoItem, Long> {

    @EntityGraph(attributePaths = {"produto", "produto.categoria", "produto.loja"})
    List<CarrinhoItem> findByClienteId(Long clienteId);

    Optional<CarrinhoItem> findByClienteIdAndProdutoId(Long clienteId, Long produtoId);

    long countByClienteId(Long clienteId);
}
